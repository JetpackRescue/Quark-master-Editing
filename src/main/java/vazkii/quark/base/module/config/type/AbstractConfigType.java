package vazkii.quark.base.module.config.type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.config.ConfigCategory;

public class AbstractConfigType implements IConfigType {

	@OnlyIn(Dist.CLIENT)
	ConfigCategory category;
	
	public AbstractConfigType() { }
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void setCategory(ConfigCategory category) {
		this.category = category;
	}
	
}
