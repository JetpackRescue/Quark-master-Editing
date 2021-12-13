package vazkii.quark.base.client.config.obj;

import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import vazkii.quark.base.client.config.ConfigCategory;

public class DoubleObject extends AbstractStringInputObject<Double> {

	public DoubleObject(ConfigValue<Double> value, String comment, Double defaultObj, Supplier<Double> objGetter, Predicate<Object> restriction, ConfigCategory parent) {
		super(value, comment, defaultObj, objGetter, restriction, parent);
	}

	@Override
	public Double fromString(String s) {
		try {
			return Double.parseDouble(s);
		} catch(NumberFormatException e) {
			return null;
		}
	}

	@Override
	public boolean isStringValid(String s) {
		return s.matches("-?[0-9]*(?:\\.[0-9]*)?");
	}

	@Override
	public int getMaxStringLength() {
		return 16;
	}

}
