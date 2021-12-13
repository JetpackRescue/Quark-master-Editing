package vazkii.quark.content.tools.module;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class AmbientDiscsModule extends QuarkModule {

	@Config public static boolean dropOnSpiderKill = true;
	
	List<Item> discs = new ArrayList<>();
	
	@Override
	public void construct() {
		disc(QuarkSounds.AMBIENT_DRIPS);
		disc(QuarkSounds.AMBIENT_OCEAN);
		disc(QuarkSounds.AMBIENT_RAIN);
		disc(QuarkSounds.AMBIENT_WIND);
		disc(QuarkSounds.AMBIENT_FIRE);
		disc(QuarkSounds.AMBIENT_CLOCK);
		disc(QuarkSounds.AMBIENT_CRICKETS);
		disc(QuarkSounds.AMBIENT_CHATTER);
	}
	
	void disc(SoundEvent sound) {
		String name = sound.getRegistryName().getPath().replaceAll(".+\\.", "");
		discs.add(new QuarkMusicDiscItem(15, () -> sound, name, this, true));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onMobDeath(LivingDeathEvent event) {
		if(dropOnSpiderKill && event.getEntity() instanceof SpiderEntity && event.getSource().getTrueSource() instanceof SkeletonEntity) {
			Item item = discs.get(event.getEntity().world.rand.nextInt(discs.size()));
			event.getEntity().entityDropItem(item, 0);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void onJukeboxLoad(JukeboxTileEntity tile) {
		Minecraft mc = Minecraft.getInstance();
		WorldRenderer render = mc.worldRenderer;
		BlockPos pos = tile.getPos();
		
		ISound sound = render.mapSoundPositions.get(pos);
		SoundHandler soundEngine = mc.getSoundHandler();
		if(sound == null || !soundEngine.isPlaying(sound)) {
			if(sound != null) {
				soundEngine.play(sound);
			} else {
				ItemStack stack = tile.getRecord();
				if(stack.getItem() instanceof QuarkMusicDiscItem)
					((QuarkMusicDiscItem) stack.getItem()).playAmbientSound(pos);
			}
		}
	}
	
}
