package vazkii.quark.content.mobs.client.layer.forgotten;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.content.mobs.entity.ForgottenEntity;

@OnlyIn(Dist.CLIENT)
public class ForgottenSheathedItemLayer<M extends EntityModel<ForgottenEntity>> extends LayerRenderer<ForgottenEntity, M> {

	public ForgottenSheathedItemLayer(IEntityRenderer<ForgottenEntity, M> p_i50919_1_) {
		super(p_i50919_1_);
	}

	@Override
	public void render(MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, ForgottenEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack item = entitylivingbaseIn.getDataManager().get(ForgottenEntity.SHEATHED_ITEM);
		
		matrix.push();
		matrix.translate(0.1, 0.2, 0.15);
		matrix.scale(0.75F, 0.75F, 0.75F);
		matrix.rotate(Vector3f.ZP.rotationDegrees(90));
		Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, item, ItemCameraTransforms.TransformType.NONE, true, matrix, bufferIn, packedLightIn);
		matrix.pop();
	}
}
