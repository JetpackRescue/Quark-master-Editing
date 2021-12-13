package vazkii.quark.base.module.config.type;

import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

public class EntitySpawnConfig extends AbstractConfigType {

	public QuarkModule module;

	@Config
	@Config.Min(value = 0, exclusive = true)
	public int spawnWeight = 40;

	@Config
	@Config.Min(1)
	public int minGroupSize = 1;

	@Config
	@Config.Min(1)
	public int maxGroupSize = 3;
	
	@Config
	public IBiomeConfig biomes;

	public EntitySpawnConfig(int spawnWeight, int minGroupSize, int maxGroupSize, IBiomeConfig biomes) {
		this.spawnWeight = spawnWeight;
		this.minGroupSize = minGroupSize;
		this.maxGroupSize = maxGroupSize;
		this.biomes = biomes;
	}
	
	public void setModule(QuarkModule module) {
		this.module = module;
	}

	public boolean isEnabled() {
		return module != null && module.enabled;
	}

}
