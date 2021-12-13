package vazkii.quark.content.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class EndermoshMusicDiscModule extends QuarkModule {

	@Config private boolean playEndermoshDuringEnderdragonFight = false;
	
	@Config private boolean addToEndCityLoot = true;
	@Config private int lootWeight = 5;
	@Config private int lootQuality = 1;

	public static QuarkMusicDiscItem endermosh;
	
	@OnlyIn(Dist.CLIENT) private boolean isFightingDragon;
	@OnlyIn(Dist.CLIENT) private int delay;
	@OnlyIn(Dist.CLIENT) private SimpleSound sound;

	@Override
	public void construct() {
		endermosh = new QuarkMusicDiscItem(14, () -> QuarkSounds.MUSIC_ENDERMOSH, "endermosh", this, false);
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if(addToEndCityLoot) {
			ResourceLocation res = event.getName();
			if(res.equals(LootTables.CHESTS_END_CITY_TREASURE)) {
				LootEntry entry = ItemLootEntry.builder(endermosh)
						.weight(lootWeight)
						.quality(lootQuality)
						.build();

				MiscUtil.addToLootTable(event.getTable(), entry);
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.END && playEndermoshDuringEnderdragonFight) {
			boolean wasFightingDragon = isFightingDragon;

			Minecraft mc = Minecraft.getInstance();
			isFightingDragon = mc.world != null 
					&& mc.world.getDimensionKey().getLocation().equals(Dimension.THE_END.getLocation())
					&& mc.ingameGUI.getBossOverlay().shouldPlayEndBossMusic();
			
			final int targetDelay = 50;
			
			if(isFightingDragon) {
				if(delay == targetDelay) {
					sound = SimpleSound.music(QuarkSounds.MUSIC_ENDERMOSH);
					mc.getSoundHandler().playDelayed(sound, 0);
					mc.ingameGUI.func_238451_a_(endermosh.getDescription());
				}

				double x = mc.player.getPosX();
				double z = mc.player.getPosZ();

				if(mc.currentScreen == null && ((x*x) + (z*z)) < 3000) // is not in screen and within island
					delay++;
				
			} else if(wasFightingDragon && sound != null) {
				mc.getSoundHandler().stop(sound);
				delay = 0;
				sound = null;
			}
		}
	}

}
