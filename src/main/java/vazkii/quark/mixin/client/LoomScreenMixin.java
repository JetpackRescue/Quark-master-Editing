package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screen.LoomScreen;
import vazkii.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(LoomScreen.class)
public class LoomScreenMixin {

	@ModifyConstant(method = "containerChange", constant = @Constant(intValue = 6))
	private static int getLimit(int curr) {
		return MoreBannerLayersModule.getLimit(curr);
	}
	
}
