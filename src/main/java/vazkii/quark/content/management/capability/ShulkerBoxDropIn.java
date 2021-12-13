package vazkii.quark.content.management.capability;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import vazkii.arl.util.AbstractDropIn;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;

public class ShulkerBoxDropIn extends AbstractDropIn {

	@Override
	public boolean canDropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot) {
		return tryAddToShulkerBox(player, stack, incoming, slot, true) != null;
	}

	@Override
	public ItemStack dropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot) {
		ItemStack ret = tryAddToShulkerBox(player, stack, incoming, slot, false); 
		return ret == null ? stack : ret;
	}

	private ItemStack tryAddToShulkerBox(PlayerEntity player, ItemStack shulkerBox, ItemStack stack, Slot slot, boolean simulate) {
		if (!SimilarBlockTypeHandler.isShulkerBox(shulkerBox) || !slot.canTakeStack(player))
			return null;

		CompoundNBT cmp = ItemNBTHelper.getCompound(shulkerBox, "BlockEntityTag", false);
		if(cmp.contains("LootTable"))
			return null;
		
		if (cmp != null) {
			TileEntity te = null;
			cmp = cmp.copy();	
			cmp.putString("id", "minecraft:shulker_box");				
			if (shulkerBox.getItem() instanceof BlockItem) {
				Block shulkerBoxBlock = Block.getBlockFromItem(shulkerBox.getItem());
				BlockState defaultState = shulkerBoxBlock.getDefaultState();
				if (shulkerBoxBlock.hasTileEntity(defaultState)) {
					te = shulkerBoxBlock.createTileEntity(defaultState, null);
					te.read(defaultState, cmp);
				}
			}

			if (te != null) {
				LazyOptional<IItemHandler> handlerHolder = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if (handlerHolder.isPresent()) {
					IItemHandler handler = handlerHolder.orElseGet(EmptyHandler::new);
					ItemStack result = ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
					boolean did = result.isEmpty() || result.getCount() != stack.getCount();

					if (did) {
						ItemStack copy = shulkerBox.copy();
						
						if(!simulate)
							stack.setCount(result.getCount());
						
						te.write(cmp);
						ItemNBTHelper.setCompound(copy, "BlockEntityTag", cmp);
						
						if(slot.isItemValid(copy))
							return copy;
					}
				}
			}
		}

		return null;
	}

}
