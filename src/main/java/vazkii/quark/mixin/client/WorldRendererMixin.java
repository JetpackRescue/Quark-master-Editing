package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import vazkii.quark.base.item.QuarkMusicDiscItem;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(method = "playRecord(Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/MusicDiscItem;)V",
			remap = false,
			at = @At(value = "JUMP", ordinal = 1),
			locals = LocalCapture.CAPTURE_FAILSOFT,
			cancellable = true)
	public void playRecord(SoundEvent soundIn, BlockPos pos, MusicDiscItem musicDiscItem, CallbackInfo info) {
		if(musicDiscItem instanceof QuarkMusicDiscItem && ((QuarkMusicDiscItem) musicDiscItem).playAmbientSound(pos))
			info.cancel();
	}


}
