package vazkii.quark.content.client.module;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.client.render.variant.*;
import vazkii.quark.mixin.RenderingRegistryAccessor;

@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class VariantAnimalTexturesModule extends QuarkModule {
	
	private static ListMultimap<VariantTextureType, ResourceLocation> textures;
	private static Map<VariantTextureType, ResourceLocation> shinyTextures;
	
	private static final int COW_COUNT = 4;
	private static final int PIG_COUNT = 3;
	private static final int CHICKEN_COUNT = 6;
	private static final int IRON_GOLEM_COUNT = 6;
	private static final int SLIME_COUNT = 2;

	@Config public static boolean enableCow = true; 
	@Config public static boolean enablePig = true; 
	@Config public static boolean enableChicken = true;
	@Config public static boolean enableShinyRabbit = true;
	@Config public static boolean enableShinyLlama = true;
	@Config public static boolean enableShinyDolphin = true;
	@Config public static boolean enableLGBTBees = true;
	@Config public static boolean enableIronGolem = true;
	@Config public static boolean enableSlime = true;
	
	@Config public static boolean everyBeeIsLGBT = false;
	
	@Config(description = "The chance for an animal to have a special \"Shiny\" skin, like a shiny pokemon. This is 1 in X. Set to 0 to disable.")
	public static int shinyAnimalChance = 2048;
	
	@Override
	public void clientSetup() {
		if(!enabled)
			return;
		
		textures = Multimaps.newListMultimap(new EnumMap<>(VariantTextureType.class), ArrayList::new);
		shinyTextures = new HashMap<>();
		
		registerTextures(VariantTextureType.COW, COW_COUNT, new ResourceLocation("textures/entity/cow/cow.png"));
		registerTextures(VariantTextureType.PIG, PIG_COUNT, new ResourceLocation("textures/entity/pig/pig.png"));
		registerTextures(VariantTextureType.CHICKEN, CHICKEN_COUNT, new ResourceLocation("textures/entity/chicken.png"));
		registerTextures(VariantTextureType.IRON_GOLEM, IRON_GOLEM_COUNT, new ResourceLocation("textures/entity/iron_golem.png"));
		registerTextures(VariantTextureType.SLIME, SLIME_COUNT, new ResourceLocation("textures/entity/slime.png"));
		registerShiny(VariantTextureType.RABBIT);
		registerShiny(VariantTextureType.LLAMA);
		registerShiny(VariantTextureType.DOLPHIN);

		if(enableCow)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.COW, VariantCowRenderer::new);
		if(enablePig)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.PIG, VariantPigRenderer::new);
		if(enableChicken)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.CHICKEN, VariantChickenRenderer::new);
		if(enableShinyRabbit)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.RABBIT, VariantRabbitRenderer::new);
		if(enableShinyLlama)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.LLAMA, VariantLlamaRenderer::new);
		if(enableIronGolem)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.IRON_GOLEM, VariantIronGolemRenderer::new);
		if(enableSlime)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.SLIME, VariantSlimeRenderer::new);
		if(enableLGBTBees)
			registerAndStackBeeRenderers();
		if(enableShinyDolphin)
			RenderingRegistry.registerEntityRenderingHandler(EntityType.DOLPHIN, VariantDolphinRenderer::new);

	}

	@SuppressWarnings("unchecked")
	private void registerAndStackBeeRenderers() {
		VariantBeeRenderer.OLD_BEE_RENDER_FACTORY = (IRenderFactory<BeeEntity>) ((RenderingRegistryAccessor) RenderingRegistryAccessor.getINSTANCE()).getEntityRenderers().get(EntityType.BEE);
		RenderingRegistry.registerEntityRenderingHandler(EntityType.BEE, VariantBeeRenderer::new);
	}

	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getTextureOrShiny(Entity e, VariantTextureType type, boolean enabled) {
		return getTextureOrShiny(e, type, () -> getRandomTexture(e, type, enabled));
	}
	
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getTextureOrShiny(Entity e, VariantTextureType type, Supplier<ResourceLocation> nonShiny) {
		UUID id = e.getUniqueID();
		long most = id.getMostSignificantBits();
		if(shinyAnimalChance > 0 && (most % shinyAnimalChance) == 0)
			return shinyTextures.get(type);
		
		return nonShiny.get();
	}
	
	@OnlyIn(Dist.CLIENT)
	private static ResourceLocation getRandomTexture(Entity e, VariantTextureType type, boolean enabled) {
		List<ResourceLocation> styles = textures.get(type);
		if(!enabled)
			return styles.get(styles.size() - 1);
		
		UUID id = e.getUniqueID();
		long most = id.getMostSignificantBits();
		int choice = Math.abs((int) (most % styles.size()));
		return styles.get(choice);
	}

	@OnlyIn(Dist.CLIENT)
	private static void registerTextures(VariantTextureType type, int count, ResourceLocation vanilla) {
		String name = type.name().toLowerCase(Locale.ROOT);
		for(int i = 1; i < count + 1; i++)
			textures.put(type, new ResourceLocation(Quark.MOD_ID, String.format("textures/model/entity/variants/%s%d.png", name, i)));
		
		if(vanilla != null)
			textures.put(type, vanilla);
		registerShiny(type);
	}
	
	private static void registerShiny(VariantTextureType type) {
		shinyTextures.put(type, new ResourceLocation(Quark.MOD_ID, String.format("textures/model/entity/variants/%s_shiny.png", type.name().toLowerCase(Locale.ROOT))));
	}

	public enum VariantTextureType {
		COW, PIG, CHICKEN, LLAMA, RABBIT, DOLPHIN, IRON_GOLEM, SLIME
	}

}
