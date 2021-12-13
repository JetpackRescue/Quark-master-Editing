package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantIronGolemRenderer extends IronGolemRenderer {

	public VariantIronGolemRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	public ResourceLocation getEntityTexture(IronGolemEntity entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.IRON_GOLEM, VariantAnimalTexturesModule.enableIronGolem);
	}
	
}
