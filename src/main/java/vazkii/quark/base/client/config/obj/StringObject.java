package vazkii.quark.base.client.config.obj;

import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import vazkii.quark.base.client.config.ConfigCategory;

public class StringObject extends AbstractStringInputObject<String> {

	public StringObject(ConfigValue<String> value, String comment, String defaultObj, Supplier<String> objGetter, Predicate<Object> restriction, ConfigCategory parent) {
		super(value, comment, defaultObj, objGetter, restriction, parent);
	}

	@Override
	protected String computeObjectString() {
		return String.format("\"%s\"", currentObj.replaceAll("\"", "\\\""));
	}

	@Override
	public String fromString(String s) {
		return s;
	}

	@Override
	public boolean isStringValid(String s) {
		return true;
	}
	
	@Override
	public int getMaxStringLength() {
		return 256;
	}
	
}
