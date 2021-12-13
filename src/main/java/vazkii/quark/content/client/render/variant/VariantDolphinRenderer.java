package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantDolphinRenderer extends DolphinRenderer {

	public VariantDolphinRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public ResourceLocation getEntityTexture(DolphinEntity entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.DOLPHIN, () -> super.getEntityTexture(entity));
	}
	
}
