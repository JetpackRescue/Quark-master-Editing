package vazkii.quark.content.world.module;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.api.distmarker.Dist;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.client.RealisticWorldType;
import vazkii.quark.content.world.gen.RealisticChunkGenerator;

@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true, subscribeOn = Dist.DEDICATED_SERVER)
public class RealisticWorldGenModule extends QuarkModule {

	public static final ResourceLocation REALISTIC_RES = new ResourceLocation("quark", "realistic");

	public static final RegistryKey<DimensionSettings> REALISTIC_KEY = RegistryKey.getOrCreateKey(Registry.NOISE_SETTINGS_KEY, REALISTIC_RES);

	@Override
	public void construct() {
		Registry.register(Registry.CHUNK_GENERATOR_CODEC, REALISTIC_RES, RealisticChunkGenerator.CODEC);
		
		RegistryHelper.register(new RealisticWorldType("realistic", false));
		RegistryHelper.register(new RealisticWorldType("realistic_large_biomes", true));
	}

}
