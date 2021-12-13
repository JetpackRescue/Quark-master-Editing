package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.inventory.container.LoomContainer;
import vazkii.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(LoomContainer.class)
public class LoomContainerMixin {

	@ModifyConstant(method = "onCraftMatrixChanged", constant = @Constant(intValue = 6))
	public int getLimit(int curr) {
		return MoreBannerLayersModule.getLimit(curr);
	}
	
}
