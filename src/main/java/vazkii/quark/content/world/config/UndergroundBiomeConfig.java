package vazkii.quark.content.world.config;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.type.ClusterSizeConfig;
import vazkii.quark.content.world.gen.underground.UndergroundBiome;

public class UndergroundBiomeConfig extends ClusterSizeConfig {

	public final UndergroundBiome biomeObj;

	public UndergroundBiomeConfig(UndergroundBiome biomeObj, int rarity, boolean isBlacklist, BiomeDictionary.Type... categories) {
		super(rarity, 26, 14, 14, 6, isBlacklist, categories);
		this.biomeObj = biomeObj;
	}

	public UndergroundBiomeConfig(UndergroundBiome biomeObj, int rarity, BiomeDictionary.Type... categories) {
		this(biomeObj, rarity, false, categories);
	}
	
	public UndergroundBiomeConfig setDefaultSize(int horizontal, int vertical, int horizontalVariation, int verticalVariation) {
		this.horizontalSize = horizontal;
		this.verticalSize = vertical;
		this.horizontalVariation = horizontalVariation;
		this.verticalVariation = verticalVariation;
		return this;
	}

}
