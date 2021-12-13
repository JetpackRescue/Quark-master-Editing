package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantSlimeRenderer extends SlimeRenderer {

	public VariantSlimeRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	public ResourceLocation getEntityTexture(SlimeEntity entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.SLIME, VariantAnimalTexturesModule.enableSlime);
	}
	
}
