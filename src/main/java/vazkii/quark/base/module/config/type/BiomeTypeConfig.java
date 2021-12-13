package vazkii.quark.base.module.config.type;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class BiomeTypeConfig extends AbstractConfigType implements IBiomeConfig {

	private final Object mutex = new Object();
	
	@Config(name = "Biome Types")
	@Config.Restriction({"hot", "cold", "sparse", "dense", "wet", "dry", "savanna", 
		"coniferous", "jungle", "spooky", "dead", "lush", "mushroom", "magical", "rare", 
		"plateau", "modified", "ocean", "river", "water", "mesa", "forest", "plains", 
		"mountain", "hills", "swamp", "sandy", "snowy", "wasteland", "beach", "void", 
		"overworld", "nether", "end"})
	private List<String> biomeTypeStrings;

	@Config
	private boolean isBlacklist;

	private List<BiomeDictionary.Type> types;

	protected BiomeTypeConfig(boolean isBlacklist, BiomeDictionary.Type... typesIn) {
		this.isBlacklist = isBlacklist;

		biomeTypeStrings = new LinkedList<>();
		for(BiomeDictionary.Type t : typesIn)
			biomeTypeStrings.add(t.getName().toLowerCase());
	}

	protected BiomeTypeConfig(boolean isBlacklist, String... types) {
		this.isBlacklist = isBlacklist;

		biomeTypeStrings = new LinkedList<>();
		biomeTypeStrings.addAll(Arrays.asList(types));
	}
	
	@Override
	public boolean canSpawn(ResourceLocation resource) {
		if(resource == null)
			return false;
		
		RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, resource);
		Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(key);

		synchronized (mutex) {
			if(types == null)
				updateTypes();
			
			for(BiomeDictionary.Type type : biomeTypes) {
				for(BiomeDictionary.Type type2 : types)
					if(type2.equals(type)) {
						return !isBlacklist;
					}
			}

			return isBlacklist;
		}
	}

	@Override
	public void onReload(ConfigFlagManager flagManager) {
		synchronized (mutex) {
			updateTypes();
		}
	}
	
	public void updateTypes() {
		types = new LinkedList<>();
		for (String s : biomeTypeStrings) {
			BiomeDictionary.Type type = BiomeDictionary.Type.getType(s);
			
			if (type != null)
				types.add(type);
		}
	}

}
