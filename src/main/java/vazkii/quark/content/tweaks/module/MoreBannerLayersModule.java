package vazkii.quark.content.tweaks.module;

import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS)
public class MoreBannerLayersModule extends QuarkModule {

	@Config
	@Config.Min(1)
	@Config.Max(16)
	public static int layerLimit = 16;
	
	private static boolean staticEnabled;
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	public static int getLimit(int curr) {
		return staticEnabled ? layerLimit : curr;
	}
	
}
