package vazkii.quark.content.tweaks.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import vazkii.arl.util.AbstractDropIn;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;

public class LavaBucketDropIn extends AbstractDropIn {

	@Override
	public boolean canDropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot) {
		return !player.isCreative() && slot.canTakeStack(player) && slot.isItemValid(stack) && !incoming.getItem().isImmuneToFire() && !SimilarBlockTypeHandler.isShulkerBox(incoming);
	}

	@Override
	public ItemStack dropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot) {
		incoming.setCount(0);
		
		if(!player.world.isRemote)
			player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 0.25F, 2F + (float) Math.random());
		
		return stack;
	}

}
