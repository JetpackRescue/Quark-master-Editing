package vazkii.quark.base.module.config.type;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class CompoundBiomeConfig extends AbstractConfigType implements IBiomeConfig {

	@Config(description = "Types of biomes this should spawn in. Must match both this and 'biomes' to spawn.")
	BiomeTypeConfig types;
	
	@Config(description = "Biome names this should spawn in. Must match both this and 'types' to spawn.")
	StrictBiomeConfig biomes;
	
	private CompoundBiomeConfig(BiomeTypeConfig types, StrictBiomeConfig biomes) {
		this.types = types;
		this.biomes = biomes;
	}
	
	public static CompoundBiomeConfig fromBiomeTypes(boolean isBlacklist, BiomeDictionary.Type... typesIn) {
		return new CompoundBiomeConfig(new BiomeTypeConfig(isBlacklist, typesIn), noSBC());
	}
	
	public static CompoundBiomeConfig fromBiomeTypeStrings(boolean isBlacklist, String... typesIn) {
		return new CompoundBiomeConfig(new BiomeTypeConfig(isBlacklist, typesIn), noSBC());
	}
	
	public static CompoundBiomeConfig fromBiomeReslocs(boolean isBlacklist, String... typesIn) {
		return new CompoundBiomeConfig(noBTC(), new StrictBiomeConfig(isBlacklist, typesIn));
	}
	
	public static CompoundBiomeConfig all() {
		return new CompoundBiomeConfig(noBTC(), noSBC());
	}
	
	private static BiomeTypeConfig noBTC() {
		return new BiomeTypeConfig(true, new BiomeDictionary.Type[0]);
	}
	
	
	private static StrictBiomeConfig noSBC() {
		return new StrictBiomeConfig(true, new String[0]);
	}
	
	@Override
	public void onReload(ConfigFlagManager flagManager) {
		types.onReload(flagManager);
		biomes.onReload(flagManager);
	}
	
	@Override
	public boolean canSpawn(ResourceLocation b) {
		return b != null && types.canSpawn(b) && biomes.canSpawn(b);
	}

}
