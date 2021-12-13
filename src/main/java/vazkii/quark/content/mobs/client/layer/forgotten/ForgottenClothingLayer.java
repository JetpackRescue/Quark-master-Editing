package vazkii.quark.content.mobs.client.layer.forgotten;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;

@OnlyIn(Dist.CLIENT)
public class ForgottenClothingLayer<T extends MobEntity & IRangedAttackMob, M extends EntityModel<T>> extends LayerRenderer<T, M> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/forgotten/overlay.png");
	private final SkeletonModel<T> layerModel = new SkeletonModel<>(0.25F, true);

	public ForgottenClothingLayer(IEntityRenderer<T, M> p_i50919_1_) {
		super(p_i50919_1_);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		renderCopyCutoutModel(getEntityModel(), layerModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
	}
}
