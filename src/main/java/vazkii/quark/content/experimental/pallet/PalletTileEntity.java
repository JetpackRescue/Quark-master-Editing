package vazkii.quark.content.experimental.pallet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.content.experimental.module.PalletModule;

public class PalletTileEntity extends TileSimpleInventory implements ITickableTileEntity {

	private static final int MAX_HEIGHT = 4;
	private static final int STACKS_PER_HEIGHT = 64;
	
	private static final int[] SLOTS = { 0, 1 };
	
	// TODO these shouldn't be public
	public ItemStack stack = ItemStack.EMPTY;
	public int count = 0;
	public int maxAcceptedCount = 0;
	public int currVisibleItems = 0;
	
	public PalletTileEntity() {
		super(PalletModule.tileEntityType);
	}
	
	@Override
	public void writeSharedNBT(CompoundNBT compound) {
		super.writeSharedNBT(compound);
		
		CompoundNBT itemNbt = new CompoundNBT();
		stack.write(itemNbt);
		
		compound.put("palletItem", itemNbt);
		compound.putInt("palletCount", count);
	}

	@Override
	public void readSharedNBT(CompoundNBT nbt) {
		super.readSharedNBT(nbt);
		
		CompoundNBT itemNbt = nbt.getCompound("palletItem");
		
		stack = ItemStack.read(itemNbt);
		count = nbt.getInt("palletCount");
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public boolean isEmpty() {
		return count == 0 || stack.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(isEmpty())
			return ItemStack.EMPTY;
		
		ItemStack ret = stack.copy();
		int maxStackSize = ret.getMaxStackSize();
		
		if(index == 0) { // out
			ret.setCount(Math.min(maxStackSize, count));
			return ret;
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(index == 0 && this.count >= count) {
			ItemStack stackAt = getStackInSlot(index).copy();
			stackAt.setCount(count);
			this.count = Math.max(0, this.count - count);
			return stackAt;
		} 
		
		else return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index == 0) {
			ItemStack stackAt = getStackInSlot(index).copy();
			this.count = Math.max(0, this.count - stack.getMaxStackSize());
			return stackAt;
		}
		
		else return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(index == 1) {
			if(isEmpty()) {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				
				this.stack = copy;
				this.count = stack.getCount();
			} else {
				ItemStack curr = getStackInSlot(index);
				if(curr.isEmpty())
					count += stack.getCount();
			}
		}
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 1 && 
				count <= maxAcceptedCount &&
				(count + itemStackIn.getCount()) <= maxAcceptedCount &&
				(isEmpty() || (ItemStack.areItemsEqual(itemStackIn, stack) && ItemStack.areItemStackTagsEqual(itemStackIn, stack)));
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {
		count = 0;
		stack = ItemStack.EMPTY;
	}

	@Override
	public void tick() {
		int height = 1;
		
		BlockPos test = pos.up();
		while(height < MAX_HEIGHT) {
			if(world.isAirBlock(test)) {
				height++;
				test = test.up();
			} else break;
		}
		
		int stackSize = stack.isEmpty() ? 64 : stack.getMaxStackSize();
		maxAcceptedCount = height * STACKS_PER_HEIGHT * stackSize;
		maxAcceptedCount = 120;
		// TODO handle placing blocks that allow you to go over the height´
		
		int prevVisibleItems = currVisibleItems;
		currVisibleItems = getDisplayedItems();
		if(prevVisibleItems != currVisibleItems && !world.isRemote)
			sync();
	}
	
	public int getDisplayedItems() {
		if(isEmpty())
			return 0;
		
		int multiplier = stack.getMaxStackSize();
		
		multiplier = 1; // TODO test
		return (int) Math.ceil((double) count / (double) multiplier); 
	}

	@Override
	public void sync() {
		MiscUtil.syncTE(this);
	}
	
}
