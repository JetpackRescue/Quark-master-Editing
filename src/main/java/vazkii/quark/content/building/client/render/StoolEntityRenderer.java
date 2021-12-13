package vazkii.quark.content.building.client.render;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.content.building.entity.StoolEntity;

public class StoolEntityRenderer extends EntityRenderer<StoolEntity> {

	public StoolEntityRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(StoolEntity entity) {
		return null;
	}
	
	@Override
	public boolean shouldRender(StoolEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
		return false;
	}
	
}
