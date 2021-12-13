package vazkii.quark.base.module.config;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class QuarkConfigBuilder implements IConfigBuilder {

	private final ForgeConfigSpec.Builder parent;
	private final IConfigCallback callback;
	
	private String currComment = "";
	
	public QuarkConfigBuilder(ForgeConfigSpec.Builder parent, IConfigCallback callback) {
		this.parent = parent;
		this.callback = callback;
	}

	@Override
	public <T> ForgeConfigSpec configure(Function<IConfigBuilder, T> func) {
		return parent.configure(b -> func.apply(this)).getRight();
	}
	
	@Override
	public void push(String s, Object holderObject) {
		parent.push(s);
		callback.push(s, currComment, holderObject);
		currComment = "";
	}

	@Override
	public void pop() {
		parent.pop();
		callback.pop();
	}

	@Override
	public void comment(String s) {
		currComment += s;
	}

	@Override
	public ConfigValue<List<?>> defineList(String name, List<?> default_, Supplier<List<?>> getter, Predicate<Object> predicate) {
		beforeDefine();
		ConfigValue<List<?>> value = parent.defineList(name, default_, predicate);
		onDefine(value, default_, getter, predicate);
		return value;
	}

	@Override
	public ConfigValue<?> defineObj(String name, Object default_, Supplier<Object> getter, Predicate<Object> predicate) {
		beforeDefine();
		ConfigValue<Object> value = parent.define(name, default_, predicate);
		onDefine(value, default_, getter, predicate);
		return value;
	}

	@Override
	public ConfigValue<Boolean> defineBool(String name, Supplier<Boolean> getter, boolean default_) {
		beforeDefine();
		ForgeConfigSpec.BooleanValue value = parent.define(name, default_);
		onDefine(value, default_, getter, o -> true);
		return value;
	}

	@Override
	public ConfigValue<Integer> defineInt(String name, Supplier<Integer> getter, int default_) {
		beforeDefine();
		ConfigValue<Integer> value = parent.define(name, default_);
		onDefine(value, default_, getter, o -> true);
		return value;
	}
	
	@Override
	public ConfigValue<Double> defineDouble(String name, Supplier<Double> getter, double default_) {
		beforeDefine();
		ConfigValue<Double> value = parent.define(name, default_);
		onDefine(value, default_, getter, o -> true);
		return value;
	}

	private void beforeDefine() {
		if(currComment.length() > 0)
			parent.comment(currComment);
	}

	private <T> void onDefine(ConfigValue<T> value, T default_, Supplier<T> getter, Predicate<Object> predicate) {
		callback.addEntry(value, default_, getter, currComment, predicate);
		currComment = "";
	}

}
