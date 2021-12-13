package vazkii.quark.content.mobs.client.render;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.content.mobs.entity.SoulBeadEntity;

public class SoulBeadRenderer extends EntityRenderer<SoulBeadEntity> {

	public SoulBeadRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(SoulBeadEntity entity) {
		return null;
	}
	
	@Override
	public boolean shouldRender(SoulBeadEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
		return false;
	}
	
}
