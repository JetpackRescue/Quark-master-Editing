package vazkii.quark.api.config;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

@OnlyIn(Dist.CLIENT)
public interface IConfigCategory extends IConfigElement {

	public IConfigCategory addCategory(String name, @Nonnull String comment, Object holderObject);
	public <T> IConfigElement addEntry(ConfigValue<T> value, T default_, Supplier<T> getter, @Nonnull String comment, @Nonnull Predicate<Object> restriction);

	public default <T> void addEntry(ConfigValue<T> forgeValue) {
		addEntry(forgeValue, forgeValue.get(), forgeValue::get, "", o -> true);
	}
	
	public default IConfigCategory addCategory(String name) {
		return addCategory(name, "", null);
	}
	
	// getters you probably don't have any use for
	public String getPath();
	public int getDepth();
	public List<IConfigElement> getSubElements();
	
	// probably stuff you shouldn't touch
	
	public void updateDirty();
	public void close();
	
	
}
