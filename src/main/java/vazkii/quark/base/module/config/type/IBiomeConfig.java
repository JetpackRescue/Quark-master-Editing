package vazkii.quark.base.module.config.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public interface IBiomeConfig extends IConfigType {

	public default boolean canSpawn(Biome b) {
		return canSpawn(b.getRegistryName());
	}
	
	public default boolean canSpawn(BiomeLoadingEvent b) {
		return canSpawn(b.getName());
	}
	
	public boolean canSpawn(ResourceLocation b);
	
}
