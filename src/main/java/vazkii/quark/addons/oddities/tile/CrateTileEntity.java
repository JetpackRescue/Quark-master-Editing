package vazkii.quark.addons.oddities.tile;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import vazkii.quark.addons.oddities.block.CrateBlock;
import vazkii.quark.addons.oddities.container.CrateContainer;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.base.handler.SortingHandler;

public class CrateTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity {

	private int totalItems = 0;
	private int numPlayersUsing;
	private List<ItemStack> stacks = new ArrayList<>();

	private LazyOptional<SidedInvWrapper> wrapper = LazyOptional.of(() -> new SidedInvWrapper(this, Direction.UP));

	private int[] visibleSlots = new int[0];
	boolean needsUpdate = false;

	protected final IIntArray crateData = new IIntArray() {
		@Override
		public int get(int index) {
			return index == 0 ? totalItems : stacks.size();
		}

		@Override
		public void set(int index, int value) {
			// NO-OP
		}
		
		@Override
		public int size() {
			return 2;
		}
	};

	public CrateTileEntity() {
		super(CrateModule.crateType);
	}

	public void spillTheTea() {
		SortingHandler.mergeStacks(stacks);

		for(ItemStack stack : stacks)
			if(!stack.isEmpty())
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
	}

	@Override
	public void tick() {
		if(needsUpdate) {
			stacks.removeIf(ItemStack::isEmpty);
			needsUpdate = false;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("totalItems", totalItems);

		ListNBT list = new ListNBT();
		for(ItemStack stack : stacks) {
			CompoundNBT stackCmp = new CompoundNBT();
			stack.write(stackCmp);
			list.add(stackCmp);
		}
		compound.put("stacks", list);

		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		totalItems = nbt.getInt("totalItems");

		ListNBT list = nbt.getList("stacks", 10);
		stacks = new ArrayList<>(list.size());
		for(int i = 0; i < list.size(); i++)
			stacks.add(ItemStack.read(list.getCompound(i)));

		super.read(state, nbt);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		if(slot < stacks.size()) {
			ItemStack stack = getStackInSlot(slot);
			totalItems -= stack.getCount();
			needsUpdate = true;

			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		ItemStack stackAt = getStackInSlot(slot);

		if(slot >= stacks.size()) {
			stacks.add(stack);
			totalItems += stack.getCount();
		} else {
			int sizeDiff = stack.getCount() - stackAt.getCount();
			totalItems += sizeDiff;
			stacks.set(slot, stack);
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		ItemStack stack = getStackInSlot(slot);
		ItemStack retstack = stack.split(count);
		totalItems -= count;

		if(stack.isEmpty())
			needsUpdate = true;

		return retstack;
	}

	@Override
	public void markDirty() {
		super.markDirty();

		totalItems = 0;
		for(ItemStack stack : stacks)
			totalItems += stack.getCount();
	}

	@Override
	public int getSizeInventory() {
		return Math.min(CrateModule.maxItems, stacks.size() + 1);
	}

	@Override
	public void clear() {
		stacks.clear();
		totalItems = 0;
	}

	@Override
	public boolean isEmpty() {
		return totalItems == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, Direction dir) {
		return (totalItems + stack.getCount()) <= CrateModule.maxItems;
	}

	@Override
	public int[] getSlotsForFace(Direction arg0) {
		if(visibleSlots.length != (stacks.size() + 1)) {
			visibleSlots = new int[stacks.size() + 1];
			for(int i = 0; i < visibleSlots.length; i++)
				visibleSlots[i] = i;
		}

		return visibleSlots;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(CrateModule.crate.getTranslationKey());
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new CrateContainer(id, player, this, crateData);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if(!removed && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return wrapper.cast();

		return super.getCapability(capability, facing);
	}

	// Vaniller copy =========================

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
		}
	}

	@Override
	public void openInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			BlockState blockstate = this.getBlockState();
			boolean flag = blockstate.get(CrateBlock.PROPERTY_OPEN);
			if (!flag) {
				this.playSound(blockstate, SoundEvents.BLOCK_BARREL_OPEN);
				this.setOpenProperty(blockstate, true);
			}

			this.scheduleTick();
		}

	}

	private void scheduleTick() {
		this.world.getPendingBlockTicks().scheduleTick(this.getPos(), this.getBlockState().getBlock(), 5);
	}

	public void crateTick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		this.numPlayersUsing = calculatePlayersUsing(this.world, this, i, j, k);
		if (this.numPlayersUsing > 0) {
			this.scheduleTick();
		} else {
			BlockState blockstate = this.getBlockState();
			if (!blockstate.isIn(CrateModule.crate)) {
				this.remove();
				return;
			}

			boolean flag = blockstate.get(CrateBlock.PROPERTY_OPEN);
			if (flag) {
				this.playSound(blockstate, SoundEvents.BLOCK_BARREL_CLOSE);
				this.setOpenProperty(blockstate, false);
			}
		}
	}

	public static int calculatePlayersUsing(World p_213976_0_, LockableTileEntity p_213976_1_, int p_213976_2_, int p_213976_3_, int p_213976_4_) {
		int i = 0;

		for(PlayerEntity playerentity : p_213976_0_.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double)((float)p_213976_2_ - 5.0F), (double)((float)p_213976_3_ - 5.0F), (double)((float)p_213976_4_ - 5.0F), (double)((float)(p_213976_2_ + 1) + 5.0F), (double)((float)(p_213976_3_ + 1) + 5.0F), (double)((float)(p_213976_4_ + 1) + 5.0F)))) {
			if (playerentity.openContainer instanceof CrateContainer) {
				IInventory iinventory = ((CrateContainer)playerentity.openContainer).crate;
				if (iinventory == p_213976_1_) {
					++i;
				}
			}
		}

		return i;
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			--this.numPlayersUsing;
		}

	}

	private void setOpenProperty(BlockState state, boolean open) {
		this.world.setBlockState(this.getPos(), state.with(CrateBlock.PROPERTY_OPEN, Boolean.valueOf(open)), 3);
	}

	private void playSound(BlockState state, SoundEvent sound) {
		double d0 = (double)this.pos.getX() + 0.5D;
		double d1 = (double)this.pos.getY() + 0.5D;
		double d2 = (double)this.pos.getZ() + 0.5D;
		this.world.playSound((PlayerEntity)null, d0, d1, d2, sound, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
	}

}
