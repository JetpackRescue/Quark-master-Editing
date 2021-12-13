package vazkii.quark.base.client.config.gui.widget;

import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.config.gui.CategoryScreen;
import vazkii.quark.base.client.config.gui.WidgetWrapper;

public interface IWidgetProvider {

	@OnlyIn(Dist.CLIENT)
	public void addWidgets(CategoryScreen parent, List<WidgetWrapper> widgets);
	
	@OnlyIn(Dist.CLIENT)
	public String getSubtitle();

}
