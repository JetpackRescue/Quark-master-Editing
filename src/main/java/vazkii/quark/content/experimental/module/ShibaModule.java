package vazkii.quark.content.experimental.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
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
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.experimental.shiba.client.render.ShibaRenderer;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false)
public class ShibaModule extends QuarkModule {

	public static EntityType<ShibaEntity> shibaType;
	
	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(40, 1, 3, CompoundBiomeConfig.fromBiomeTypes(false, BiomeDictionary.Type.MOUNTAIN));
	
	@Override
	public void construct() {
		shibaType = EntityType.Builder.create(ShibaEntity::new, EntityClassification.CREATURE)
				.size(0.8F, 0.8F)
				.trackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new ShibaEntity(shibaType, world))
				.build("shiba");
		RegistryHelper.register(shibaType, "shiba");
		
		EntitySpawnHandler.registerSpawn(this, shibaType, EntityClassification.CREATURE, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn, spawnConfig);
		EntitySpawnHandler.addEgg(shibaType, 0xa86741, 0xe8d5b6, spawnConfig);
	}
	
	@Override
	public void setup() {
		GlobalEntityTypeAttributes.put(shibaType, WolfEntity.func_234233_eS_().create());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(shibaType, ShibaRenderer::new);
	}
	
}
