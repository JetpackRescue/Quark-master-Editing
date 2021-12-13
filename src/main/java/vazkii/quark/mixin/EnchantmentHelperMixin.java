package vazkii.quark.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import vazkii.quark.content.tools.module.AncientTomesModule;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

	@Inject(method = "getEnchantments", at = @At("HEAD"), cancellable = true)
	private static void getAncientTomeEnchantments(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> callbackInfoReturnable) {
		Enchantment enchant = AncientTomesModule.getTomeEnchantment(stack);

		if(enchant != null) {
			Map<Enchantment, Integer> map = new HashMap<>();
			map.put(enchant, 1);
			callbackInfoReturnable.setReturnValue(map);
		}
	}
}
