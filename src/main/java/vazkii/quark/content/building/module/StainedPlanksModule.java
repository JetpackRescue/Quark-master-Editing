package vazkii.quark.content.building.module;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.BUILDING)
public class StainedPlanksModule extends QuarkModule {

	public static Map<DyeColor, Block> blocks = new HashMap<>();
	
	@Override
	public void construct() {
		for(DyeColor dye : DyeColor.values()) {
			Block.Properties props = AbstractBlock.Properties.create(Material.WOOD, dye.getMapColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD);
			QuarkBlock b = new QuarkBlock(dye.getTranslationKey() + "_stained_planks", this, ItemGroup.BUILDING_BLOCKS, props);
			VariantHandler.addSlabAndStairs(b);
			blocks.put(dye, b);
		}
	}
	
}
