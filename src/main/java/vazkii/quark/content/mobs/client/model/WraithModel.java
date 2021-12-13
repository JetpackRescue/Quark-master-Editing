package vazkii.quark.content.mobs.client.model;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.quark.content.mobs.entity.WraithEntity;

public class WraithModel extends EntityModel<WraithEntity> {
	
	public final ModelRenderer body;
	public final ModelRenderer rightArm;
	public final ModelRenderer leftArm;
	
	WraithEntity wraith;
	double offset;
	float alphaMult;

	public WraithModel() {
		super(RenderType::getEntityTranslucent);
		
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.body = new ModelRenderer(this, 0, 0);
		this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body.addBox(-4.0F, -8.0F, -4.0F, 8, 24, 8, 0.0F);
		this.leftArm = new ModelRenderer(this, 32, 16);
		this.leftArm.mirror = true;
		this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		this.rightArm = new ModelRenderer(this, 32, 16);
		this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
	}

	@Override
	public void setRotationAngles(WraithEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		Random rng = new Random(entity.getEntityId());
		float time = ageInTicks + rng.nextInt(10000000);
		
		leftArm.rotateAngleX = (float) Math.toRadians(-50F + rng.nextFloat() * 20F);
		rightArm.rotateAngleX = (float) Math.toRadians(-50F + rng.nextFloat() * 20F);
		leftArm.rotateAngleZ = (float) Math.toRadians(-110F + (float) Math.cos(time / (8 + rng.nextInt(2))) * (8F + rng.nextFloat() * 8F));
		rightArm.rotateAngleZ = (float) Math.toRadians(110F + (float) Math.cos((time + 300) / (8 + rng.nextInt(2))) * (8F + rng.nextFloat() * 8F));
		
		wraith = entity;
		offset = Math.sin(time / 16) * 0.1 + 0.15;
		alphaMult = 0.5F + (float) Math.sin(time / 20)  * 0.3F;
	}
	
	@Override
	public void render(MatrixStack matrix, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		alpha *= alphaMult;
		
		matrix.push();
		matrix.translate(0, offset, 0);
		body.render(matrix, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftArm.render(matrix, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightArm.render(matrix, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		
		for(int i = 0; i < 6; i++) {
			alpha *= 0.6;
			matrix.translate(0, 0, 1.5 * offset + 0.1);
			matrix.scale(0.8F, 0.8F, 0.8F);
			matrix.rotate(Vector3f.XP.rotationDegrees(60F * (float) offset));
			body.render(matrix, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			leftArm.render(matrix, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			rightArm.render(matrix, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		matrix.pop();
		
	}

}
