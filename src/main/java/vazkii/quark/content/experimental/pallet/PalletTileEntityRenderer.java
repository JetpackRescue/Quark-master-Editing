package vazkii.quark.content.experimental.pallet;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class PalletTileEntityRenderer extends TileEntityRenderer<PalletTileEntity> {

	public PalletTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(PalletTileEntity pallet, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
		if(!pallet.isEmpty()) {
			int displayed = pallet.getDisplayedItems();
			ItemStack stack = pallet.stack;
			ItemRenderer render = Minecraft.getInstance().getItemRenderer();
			
			Random rand = new Random(pallet.getPos().hashCode());
			matrix.push();
			matrix.translate(0.5, 1F / 16F, 0.5);
			matrix.scale(0.75F, 0.5F, 0.75F);
			matrix.rotate(Vector3f.XP.rotationDegrees(90F));
			
			for(int i = 0; i < displayed; i++) {
				render.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, light, overlay, matrix, buffer);

				float ampl = 40F;
				float rot = (rand.nextFloat() * ampl) - (ampl / 2F);
				matrix.rotate(Vector3f.ZP.rotationDegrees(rot));
				
				matrix.translate(0, 0, -1F / 16F);
			}
			matrix.pop();
		}
		
	}

}
