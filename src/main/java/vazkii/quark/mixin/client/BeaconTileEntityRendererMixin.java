package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.tileentity.BeaconTileEntity;
import vazkii.quark.content.world.client.render.QuarkBeaconTileEntityRenderer;

@Mixin(BeaconTileEntityRenderer.class)
public class BeaconTileEntityRendererMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(BeaconTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, CallbackInfo callbackInfo) {
		if(QuarkBeaconTileEntityRenderer.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn))
			callbackInfo.cancel();
	}


}
