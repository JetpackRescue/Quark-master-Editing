package vazkii.quark.content.world.config;

import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.AbstractConfigType;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.DimensionConfig;

public class BlossomTreeConfig extends AbstractConfigType {

	@Config
	public DimensionConfig dimensions = DimensionConfig.overworld(false);
	
	@Config
	public CompoundBiomeConfig biomeConfig;
	
	@Config
	public int rarity;
	
	public BlossomTreeConfig(int rarity, BiomeDictionary.Type type) {
		this.rarity = rarity;
		biomeConfig = CompoundBiomeConfig.fromBiomeTypes(false, type);
	}
	
}
