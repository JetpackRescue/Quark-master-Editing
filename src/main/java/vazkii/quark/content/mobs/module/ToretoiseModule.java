package vazkii.quark.content.mobs.module;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.ToretoiseRenderer;
import vazkii.quark.content.mobs.entity.ToretoiseEntity;

import java.util.List;

@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class ToretoiseModule extends QuarkModule {

	public static EntityType<ToretoiseEntity> toretoiseType;
	
	@Config public static int maxYLevel = 32;
	
	@Config(description="The number of ticks from mining a tortoise until feeding it could cause it to regrow.")
	public static int cooldownTicks = 20 * 60;
	
	@Config(description="The items that can be fed to toretoises to make them regrow ores.")
	public static List<String> foods = Lists.newArrayList("quark:root_item", "minecraft:cactus");
	
	@Config(description="Feeding a toretoise after cooldown will regrow them with a one-in-this-number chance. "
			+ "Set to 1 to always regrow, or 0 to disable.")
	public static int regrowChance = 3;
	
	@Config
	public static DimensionConfig dimensions = DimensionConfig.overworld(false);
	
	@Config 
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(100, 1, 1, CompoundBiomeConfig.fromBiomeTypes(true, BiomeDictionary.Type.VOID, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END));
	
	@Override
	public void construct() {
		toretoiseType = EntityType.Builder.<ToretoiseEntity>create(ToretoiseEntity::new, EntityClassification.CREATURE)
				.size(2F, 1F)
				.trackingRange(8)
				.immuneToFire()
				.setCustomClientFactory((spawnEntity, world) -> new ToretoiseEntity(toretoiseType, world))
				.build("toretoise");

		RegistryHelper.register(toretoiseType, "toretoise");
		
		EntitySpawnHandler.registerSpawn(this, toretoiseType, EntityClassification.MONSTER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, ToretoiseEntity::spawnPredicate, spawnConfig);
		EntitySpawnHandler.addEgg(toretoiseType, 0x55413b, 0x383237, spawnConfig);
	}
	
	@Override
	public void setup() {
		GlobalEntityTypeAttributes.put(toretoiseType, ToretoiseEntity.prepareAttributes().create());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(toretoiseType, ToretoiseRenderer::new);
	}
	
}
