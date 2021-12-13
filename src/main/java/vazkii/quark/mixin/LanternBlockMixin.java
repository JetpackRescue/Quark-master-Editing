package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import vazkii.quark.content.building.module.WoodenPostsModule;

@Mixin(LanternBlock.class)
public class LanternBlockMixin {

	@Inject(method = "isValidPosition", at = @At("RETURN"), cancellable = true)
	private void isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(WoodenPostsModule.canLanternConnect(state, worldIn, pos, callbackInfoReturnable.getReturnValue()));
	}
	
}
