package vazkii.quark.addons.oddities.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.addons.oddities.tile.CrateTileEntity;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.ScrollCrateMessage;

public class CrateContainer extends Container {

	public final CrateTileEntity crate;
	public final PlayerInventory playerInv;

	public static final int numRows = 6;
	public static final int numCols = 9;
	public static final int displayedSlots = numCols * numRows;

	public int scroll = 0;
	private final IIntArray crateData;

	public CrateContainer(int id, PlayerInventory inv, CrateTileEntity crate) {
		this(id, inv, crate, new IntArray(2));
	}

	public CrateContainer(int id, PlayerInventory inv, CrateTileEntity crate, IIntArray data) {
		super(CrateModule.containerType, id);
		crate.openInventory(inv.player);

		this.crate = crate;
		this.playerInv = inv;
		this.crateData = data;

		int i = (numRows - 4) * 18;

		for(int j = 0; j < numRows; ++j)
			for(int k = 0; k < numCols; ++k)
				addSlot(new CrateSlot(k + j * numCols, 8 + k * 18, 18 + j * 18));

		for(int l = 0; l < 3; ++l)
			for(int j1 = 0; j1 < 9; ++j1)
				addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));

		for(int i1 = 0; i1 < 9; ++i1)
			addSlot(new Slot(inv, i1, 8 + i1 * 18, 161 + i));

		trackIntArray(crateData);
	}

	public int getTotal() {
		return crateData.get(0);
	}

	public int getStackCount() {
		return crateData.get(1);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			boolean empty = false;

			if (index < displayedSlots) {
				if(!this.mergeItemStack(itemstack1, displayedSlots, inventorySlots.size(), true))
					empty = true;
				crate.markDirty();
			} else {
				if(MiscUtil.canPutIntoInv(itemstack, crate, Direction.UP, true)) {
					MiscUtil.putIntoInv(itemstack, crate, Direction.UP, false, false);
					itemstack1.setCount(0);
					empty = true;
				} else return ItemStack.EMPTY;
			}

			if(itemstack1.isEmpty()) {
				if(slot instanceof CrateSlot) {
					CrateSlot cslot = (CrateSlot) slot;
					int target = cslot.getTarget();
					crate.removeStackFromSlot(target);
				}
				else slot.putStack(ItemStack.EMPTY);
			} else if(!empty) {
				slot.onSlotChanged();
				forceSync();
			}

			if(empty)
				return ItemStack.EMPTY;
		}

		return itemstack;
	}

	public static CrateContainer fromNetwork(int windowId, PlayerInventory playerInventory, PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		CrateTileEntity te = (CrateTileEntity) playerInventory.player.world.getTileEntity(pos);
		return new CrateContainer(windowId, playerInventory, te);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return crate.isUsableByPlayer(playerIn);
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		crate.closeInventory(playerIn);
	}

	private void forceSync() {
		World world = crate.getWorld();
		if(!world.isRemote)
			for(IContainerListener icontainerlistener : listeners)
				icontainerlistener.sendAllContents(this, getInventory());
	}

	public void scroll(boolean down, boolean packet) {
		boolean did = false;

		if(down) {
			int maxScroll = (getStackCount() / numCols) * numCols;

			int target = scroll + numCols;
			if(target <= maxScroll) {
				scroll = target;
				did = true;
			}
		} else {
			int target = scroll - numCols;
			if(target >= 0) {
				scroll = target;
				did = true;
			}
		}

		if(did) {
			detectAndSendChanges();

			if(packet)
				QuarkNetwork.sendToServer(new ScrollCrateMessage(down));
		}
	}

	private class CrateSlot extends Slot {

		private final int index;

		public CrateSlot(int index, int xPosition, int yPosition) {
			super(crate, index, xPosition, yPosition);

			this.index = index;
		}

		private int getTarget() {
			World world = crate.getWorld();
			return (world.isRemote ? index : (index + scroll));
		}

		@Override
		public ItemStack getStack() {
			int targetIndex = getTarget();
			int size = crate.getSizeInventory();
			if(targetIndex >= size)
				return ItemStack.EMPTY;

			return crate.getStackInSlot(targetIndex);
		}

		@Override
		public void putStack(ItemStack stack) {
			int targetIndex = getTarget();
			inventory.setInventorySlotContents(targetIndex, stack);

			onSlotChanged();
			forceSync();
		}

		@Override
		public ItemStack decrStackSize(int amount) {
			return inventory.decrStackSize(getTarget(), amount);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack.getCount() + getTotal() <= CrateModule.maxItems;
		}

	}
}
