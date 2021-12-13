package vazkii.quark.content.experimental.shiba.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

public class ShibaModel extends EntityModel<ShibaEntity> {
	
	private final ModelRenderer main;

	private final ModelRenderer head;
	private final ModelRenderer rEar;
	private final ModelRenderer lEar;
	
	private final ModelRenderer tongue;
	private final ModelRenderer torso;
	private final ModelRenderer tail;
	
	private final ModelRenderer rFrontLeg;
	private final ModelRenderer lFrontLeg;
	private final ModelRenderer rBackLeg;
	private final ModelRenderer lBackLeg;
	
	private ShibaEntity entity;
	
	public ShibaModel() {
		textureWidth = 80;
		textureHeight = 48;
		
		main = new ModelRenderer(this);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 15.0F, -5.0F);
		head.setTextureOffset(16, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 11.0F, 6.0F, 0.0F, false);
		head.setTextureOffset(44, 0).addBox(-1.5F, -6.0F, -8.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

		rEar = new ModelRenderer(this);
		rEar.setRotationPoint(3.0F, -12.0F, 2.0F);
		head.addChild(rEar);
		rEar.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);

		lEar = new ModelRenderer(this);
		lEar.setRotationPoint(-3.0F, -12.0F, 2.0F);
		head.addChild(lEar);
		lEar.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, 0.0F, true);

		tongue = new ModelRenderer(this);
		tongue.setRotationPoint(0.0F, -4.0F, -8.0F);
		head.addChild(tongue);
		tongue.setTextureOffset(36, 34).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 0.0F, 3.0F, 0.0F, false);
		
		torso = new ModelRenderer(this);
		torso.setRotationPoint(0.0F, 13.0F, -7.0F);
		torso.setTextureOffset(36, 10).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 14.0F, 8.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 14.0F, 4.0F);
		torso.addChild(tail);
		tail.setTextureOffset(0, 32).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, 0.0F, false);

		rFrontLeg = new ModelRenderer(this);
		rFrontLeg.setRotationPoint(3.0F, 16.0F, -5.0F);
		rFrontLeg.setTextureOffset(0, 21).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, false);

		lFrontLeg = new ModelRenderer(this);
		lFrontLeg.setRotationPoint(-3.0F, 16.0F, -5.0F);
		lFrontLeg.setTextureOffset(0, 21).addBox(-1.0F, 0.0F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, true);

		rBackLeg = new ModelRenderer(this);
		rBackLeg.setRotationPoint(3.0F, 15.0F, 4.0F);
		rBackLeg.setTextureOffset(12, 18).addBox(-2.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, false);

		lBackLeg = new ModelRenderer(this);
		lBackLeg.setRotationPoint(-3.0F, 15.0F, 4.0F);
		lBackLeg.setTextureOffset(12, 18).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, true);
		
		main.addChild(head);
		main.addChild(torso);
		main.addChild(rFrontLeg);
		main.addChild(rBackLeg);
		main.addChild(lFrontLeg);
		main.addChild(lBackLeg);
	}
	
	public void transformToHead(MatrixStack matrix) {
		head.translateRotate(matrix);
	}

	@Override
	public void setLivingAnimations(ShibaEntity shiba, float limbSwing, float limbSwingAmount, float partialTickTime) {
		this.entity = shiba;

		setRotationAngle(rFrontLeg, MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
		setRotationAngle(lFrontLeg, MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
		setRotationAngle(rBackLeg, MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
		setRotationAngle(lBackLeg, MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
	}
	
	@Override
	public void setRotationAngles(ShibaEntity shiba, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
		main.setRotationPoint(0F, 0F, 0F);
		lBackLeg.setRotationPoint(-3.0F, 15.0F, 4.0F);
		rBackLeg.setRotationPoint(3.0F, 15.0F, 4.0F);
		
		setRotationAngle(main, 0, 0F, 0F);
		setRotationAngle(torso, 1.5708F, 0F, 0F);

		setRotationAngle(head, MathHelper.cos(ageInTicks * 0.6F) * 0.01F, yaw * 0.017453292F, MathHelper.sin(ageInTicks * 0.06F) * 0.06F);
		
		setRotationAngle(tail, MathHelper.cos(ageInTicks * 0.1F) * 0.1F, MathHelper.sin(ageInTicks * 0.15F) * 0.12F, MathHelper.cos(ageInTicks * 0.3F) * 0.2F);
		setRotationAngle(lEar, 0F, MathHelper.cos(ageInTicks * 0.08F) * 0.05F - 0.05F, 0F);
		setRotationAngle(rEar, 0F, MathHelper.sin(ageInTicks * 0.07F) * 0.05F + 0.05F, 0F);
		
		boolean tongueOut = false;
		
		BlockState state = shiba.getBlockState();
		boolean sleep = state.isIn(BlockTags.BEDS);

		if(shiba.isSleeping()) {
			tongueOut = true;

			if(sleep) {
				main.setRotationPoint(16F, 18.0F, 0F);
				setRotationAngle(main, 0F, 0F, 1.5708F);
				
				setRotationAngle(lBackLeg, MathHelper.cos(ageInTicks * 0.2F) * 0.1F, 0F, MathHelper.sin(ageInTicks * 0.18F) * 0.12F);
				setRotationAngle(rBackLeg, MathHelper.sin(ageInTicks * 0.22F) * 0.08F, 0F, MathHelper.cos(ageInTicks * 0.16F) * 0.11F);
				
				setRotationAngle(rFrontLeg, MathHelper.cos(ageInTicks * 0.19F) * 0.1F, 0F, MathHelper.sin(ageInTicks * 0.21F) * 0.12F);
				setRotationAngle(lFrontLeg, MathHelper.sin(ageInTicks * 0.18F) * 0.08F, 0F, MathHelper.cos(ageInTicks * 0.2F) * 0.11F);
			} else {
				setRotationAngle(torso, 1F, 0F, 0F);
				
				lBackLeg.setRotationPoint(-3.0F, 19.0F, 2.0F);
				rBackLeg.setRotationPoint(3.0F, 19.0F, 2.0F);

				setRotationAngle(lBackLeg, -1F, -0.5F, 0F);
				setRotationAngle(rBackLeg, -1F, -0.5F, 0F);
				
				setRotationAngle(lFrontLeg, -0.5F, 0.5F, 0F);
				setRotationAngle(rFrontLeg, -0.5F, 0.5F, 0F);
			}
		}
		
		if(tongueOut && shiba.getMouthItem().isEmpty()) {
			tongue.setRotationPoint(0F, -4F, -6.75F + MathHelper.cos(ageInTicks * 0.19F) * 0.25F);
			setRotationAngle(tongue, MathHelper.cos(ageInTicks * 0.19F) * 0.1F + 0.2F, 0, 0);
		} else {
			tongue.setRotationPoint(0F, -4F, -5F);
			setRotationAngle(tongue, 0, 0, 0);
		}
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		matrixStack.push();
		if(entity.isSleeping())
			matrixStack.translate(0, 0.12, 0);
		
		main.translateRotate(matrixStack);
		
		matrixStack.push();
		if(isChild)
			matrixStack.translate(0.0F, 5.0F / 16F, 0F);
		
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		matrixStack.pop();

		matrixStack.push();
		if (isChild) {
			matrixStack.translate(0.0F, 12.0F / 16F, 0F);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
		}
		
		torso.render(matrixStack, buffer, packedLight, packedOverlay);
		rFrontLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		rBackLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		lFrontLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		lBackLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		matrixStack.pop();
		matrixStack.pop();
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
