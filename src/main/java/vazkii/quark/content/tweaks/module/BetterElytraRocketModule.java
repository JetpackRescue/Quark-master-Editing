package vazkii.quark.content.tweaks.module;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class BetterElytraRocketModule extends QuarkModule {

	@SubscribeEvent
	public void onUseRocket(PlayerInteractEvent.RightClickItem event) {
		PlayerEntity player = event.getPlayer();
		if(!player.isElytraFlying() && player.getItemStackFromSlot(EquipmentSlotType.CHEST).canElytraFly(player)) {
			World world = player.world;
			ItemStack itemstack = event.getItemStack();

			if(itemstack.getItem() instanceof FireworkRocketItem) {
				if(!world.isRemote) {
					world.addEntity(new FireworkRocketEntity(world, itemstack, player));
					if(!player.abilities.isCreativeMode)
						itemstack.shrink(1);
				}
				
				player.startFallFlying();
				player.jump();

				event.setCanceled(true);
				event.setCancellationResult(world.isRemote ? ActionResultType.SUCCESS : ActionResultType.CONSUME);
			}

		}

	}

}
