package vazkii.quark.content.world.config;

import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.AbstractConfigType;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.OrePocketConfig;

public class StoneTypeConfig extends AbstractConfigType {

	@Config
	public DimensionConfig dimensions;
	@Config
	public OrePocketConfig oregen = new OrePocketConfig(0, 255, 33, 10);

	public StoneTypeConfig(DimensionConfig config) {
		dimensions = config;
	}
	
	public StoneTypeConfig() {
		this(DimensionConfig.overworld(false));
	}

}
