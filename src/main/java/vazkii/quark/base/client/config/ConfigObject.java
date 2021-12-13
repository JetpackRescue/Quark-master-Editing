package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.api.config.IConfigObject;
import vazkii.quark.base.client.config.gui.widget.IWidgetProvider;
import vazkii.quark.base.client.config.obj.BooleanObject;
import vazkii.quark.base.client.config.obj.DoubleObject;
import vazkii.quark.base.client.config.obj.IntegerObject;
import vazkii.quark.base.client.config.obj.ListObject;
import vazkii.quark.base.client.config.obj.StringObject;

public abstract class ConfigObject<T> extends AbstractConfigElement implements IConfigObject<T>, IWidgetProvider {

	public final T defaultObj;
	public final Predicate<Object> restriction;
	protected final Supplier<T> objectGetter;
	protected final String displayName;
	
	protected T loadedObj;
	protected T currentObj;

	protected final ConfigValue<T> configValue;
	
	public ConfigObject(ConfigValue<T> value, String comment, T defaultObj, Supplier<T> objGetter, Predicate<Object> restriction, ConfigCategory parent) {
		super(getDefaultName(value), comment, parent);
		this.defaultObj = defaultObj;
		this.objectGetter = objGetter;
		this.restriction = restriction;
		this.configValue = value;
		
		if(name.contains(" "))
			displayName = String.format("\"%s\"", name);
		else displayName = name;
	}

	@SuppressWarnings("unchecked")
	public static <T> IConfigObject<T> create(ConfigValue<T> value, String comment, T defaultObj, Supplier<T> objGetter, Predicate<Object> restriction, ConfigCategory parent) {
		if(defaultObj instanceof Boolean)
			return (IConfigObject<T>) new BooleanObject((ConfigValue<Boolean>) value, comment, (Boolean) defaultObj, (Supplier<Boolean>) objGetter, restriction, parent);
		
		else if(defaultObj instanceof String)
			return (IConfigObject<T>) new StringObject((ConfigValue<String>) value, comment, (String) defaultObj, (Supplier<String>) objGetter, restriction, parent);

		else if(defaultObj instanceof Integer)
			return (IConfigObject<T>) new IntegerObject((ConfigValue<Integer>) value, comment, (Integer) defaultObj, (Supplier<Integer>) objGetter, restriction, parent);
		
		else if(defaultObj instanceof Double)
			return (IConfigObject<T>) new DoubleObject((ConfigValue<Double>) value, comment, (Double) defaultObj, (Supplier<Double>) objGetter, restriction, parent);
		
		else if(defaultObj instanceof List)
			return (IConfigObject<T>) new ListObject((ConfigValue<List<String>>) value, comment, (List<String>) defaultObj, (Supplier<List<String>>) objGetter, restriction, parent);
		
		else throw new IllegalArgumentException(defaultObj + " isn't a valid config object.");
	}
	
	@Override
	public String getGuiDisplayName() {
		return name;
	}

	@Override
	public void refresh() {
		currentObj = objectGetter.get();
		loadedObj = currentObj;
	}
	
	@Override
	public void clean() {
		loadedObj = currentObj;
	}

	@Override
	public void save() {
		configValue.set(currentObj);
	}

	@Override
	public void reset(boolean hard) {
		setCurrentObj(hard ? defaultObj : loadedObj);
	}
	
	@Override
	public T getCurrentObj() {
		return currentObj;
	}
	
	@Override
	public void setCurrentObj(T currentObj) {
		this.currentObj = currentObj;
		parent.updateDirty();
	}

	@Override
	public String getSubtitle() {
		String str = currentObj.toString();
		if(str.length() > 30)
			str = str.substring(0, 27) + "...";
		return str;
	}
	
	@Override
	public boolean isDirty() {
		return wouldBeDirty(currentObj);
	}
	
	public boolean wouldBeDirty(T testObject) {
		return !loadedObj.equals(testObject);
	}
	
	@Override
	public void print(String pad, PrintStream out) {
		super.print(pad, out);
		
		String objStr = computeObjectString();;
		out.printf("%s%s = %s%n", pad, displayName, objStr);
	}
	
	protected String computeObjectString() {
		return currentObj.toString();
	}

	@Override
	public int compareTo(IConfigElement o) {
		if(o == this)
			return 0;
		if(!(o instanceof IConfigObject))
			return -1;
		
		return getName().compareTo(o.getName());
	}

	private static String getDefaultName(ConfigValue<?> value) {
		List<String> path = value.getPath();
		return path.get(path.size() - 1);
	}
	
}
