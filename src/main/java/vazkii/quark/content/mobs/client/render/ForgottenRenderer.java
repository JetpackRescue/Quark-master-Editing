package vazkii.quark.content.mobs.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.content.mobs.client.layer.forgotten.ForgottenClothingLayer;
import vazkii.quark.content.mobs.client.layer.forgotten.ForgottenEyesLayer;
import vazkii.quark.content.mobs.client.layer.forgotten.ForgottenSheathedItemLayer;

public class ForgottenRenderer extends SkeletonRenderer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/forgotten/main.png");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ForgottenRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		addLayer(new ForgottenClothingLayer<>(this));
		addLayer(new ForgottenEyesLayer(this));
		addLayer(new ForgottenSheathedItemLayer(this));
	}

	@Override
	public ResourceLocation getEntityTexture(AbstractSkeletonEntity entity) {
		return TEXTURE;
	}

	@Override
	protected void preRenderCallback(AbstractSkeletonEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(1.2F, 1.2F, 1.2F);
	}

}
