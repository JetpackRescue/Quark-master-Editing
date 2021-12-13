package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.advancements.PlayerAdvancements;
import vazkii.quark.base.handler.GeneralConfig;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {

	@ModifyConstant(method = "shouldBeVisible", constant = @Constant(intValue = 2))
	public int visibility(int curr) {
		return GeneralConfig.advancementVisibilityDepth;
	}
	
}
