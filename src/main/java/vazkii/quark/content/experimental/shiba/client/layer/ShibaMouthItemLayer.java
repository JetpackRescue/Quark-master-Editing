package vazkii.quark.content.experimental.shiba.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.quark.content.experimental.shiba.client.model.ShibaModel;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

public class ShibaMouthItemLayer extends LayerRenderer<ShibaEntity, ShibaModel> {

	public ShibaMouthItemLayer(IEntityRenderer<ShibaEntity, ShibaModel> p_i50919_1_) {
		super(p_i50919_1_);
	}

	@Override
	public void render(MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, ShibaEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack item = entitylivingbaseIn.getMouthItem();
		if(item.isEmpty())
			return;
		
		boolean sword = item.getItem() instanceof SwordItem;
		boolean trident = item.getItem() instanceof TridentItem;
		float scale = sword || trident ? 0.75F : 0.5F;
		matrix.push();
		getEntityModel().transformToHead(matrix);
		
		if(sword)
			matrix.translate(0.3, -0.15, -0.5);
		else if(trident) {
			matrix.translate(1, -0.6, -0.7);
			matrix.rotate(Vector3f.YP.rotationDegrees(40F));
		} else
			matrix.translate(0, -0.15, -0.5);
		matrix.scale(scale, scale, scale);

		matrix.rotate(Vector3f.YP.rotationDegrees(45));
		matrix.rotate(Vector3f.XP.rotationDegrees(90));
		Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, item, ItemCameraTransforms.TransformType.NONE, true, matrix, bufferIn, packedLightIn);
		matrix.pop();
	}
}