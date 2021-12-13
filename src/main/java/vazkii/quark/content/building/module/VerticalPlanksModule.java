package vazkii.quark.content.building.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.BUILDING)
public class VerticalPlanksModule extends QuarkModule {

	@Override
	public void construct() {
		for(int i = 0; i < MiscUtil.OVERWORLD_WOOD_TYPES.length; i++)
			new QuarkBlock("vertical_" + MiscUtil.OVERWORLD_WOOD_TYPES[i] + "_planks", this, ItemGroup.BUILDING_BLOCKS, Block.Properties.from(MiscUtil.OVERWORLD_WOOD_OBJECTS[i]));
		
		for(int i = 0; i < MiscUtil.NETHER_WOOD_TYPES.length; i++)
			new QuarkBlock("vertical_" + MiscUtil.NETHER_WOOD_TYPES[i] + "_planks", this, ItemGroup.BUILDING_BLOCKS, Block.Properties.from(MiscUtil.NETHER_WOOD_OBJECTS[i]));
	}
	
	@Override
	public void modulesStarted() {
		for(DyeColor dye : DyeColor.values())
			new QuarkBlock("vertical_" + dye.getTranslationKey() + "_stained_planks", this, ItemGroup.BUILDING_BLOCKS, Block.Properties.from(StainedPlanksModule.blocks.get(dye)))
			.setCondition(() -> ModuleLoader.INSTANCE.isModuleEnabled(StainedPlanksModule.class));
	}

}
