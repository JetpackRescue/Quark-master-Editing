package vazkii.quark.content.building.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class CelebratoryLampsModule extends QuarkModule {

	@Config
	public static int lightLevel = 15;
	
	private static Block stone_lamp, stone_brick_lamp;
	
	@Override
	public void construct() {
		stone_lamp = new QuarkBlock("stone_lamp", this, ItemGroup.BUILDING_BLOCKS, Block.Properties.from(Blocks.STONE).setLightLevel(s -> lightLevel));
		stone_brick_lamp = new QuarkBlock("stone_brick_lamp", this, ItemGroup.BUILDING_BLOCKS, Block.Properties.from(Blocks.STONE_BRICKS).setLightLevel(s -> lightLevel));
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		if(event.getFlags().isAdvanced()) {
			ItemStack stack = event.getItemStack();
			Item item = stack.getItem();
			if(item == stone_lamp.asItem() || item == stone_brick_lamp.asItem())
				event.getToolTip().add(1, new TranslationTextComponent("quark.misc.celebration").mergeStyle(TextFormatting.GRAY));
		}
	}
	
}
