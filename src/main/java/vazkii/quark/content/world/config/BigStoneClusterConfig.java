package vazkii.quark.content.world.config;

import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.BiomeTypeConfig;
import vazkii.quark.base.module.config.type.ClusterSizeConfig;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.IBiomeConfig;

public class BigStoneClusterConfig extends ClusterSizeConfig {

	@Config
	public boolean enabled = true;

	public BigStoneClusterConfig(BiomeDictionary.Type... types) {
		this(DimensionConfig.overworld(false), 14, 9, 4, 20, 80, CompoundBiomeConfig.fromBiomeTypes(false, types));
	}

	public BigStoneClusterConfig(DimensionConfig dimensions, int clusterSize, int sizeVariation, int rarity, int minYLevel, int maxYLevel, IBiomeConfig biomes) {
		super(rarity, clusterSize, clusterSize, sizeVariation, sizeVariation, biomes);
		this.dimensions = dimensions;
		
		this.minYLevel = minYLevel;
		this.maxYLevel = maxYLevel;
	}
	
	public BigStoneClusterConfig setVertical(int vertical, int verticalVariation) {
		this.verticalSize = vertical;
		this.verticalVariation = verticalVariation;
		return this;
	}
	

}
