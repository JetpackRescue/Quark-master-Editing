package vazkii.quark.content.building.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.HedgeBlock;
import vazkii.quark.content.world.block.BlossomSaplingBlock.BlossomTree;
import vazkii.quark.content.world.module.BlossomTreesModule;

@LoadModule(category = ModuleCategory.BUILDING)
public class HedgesModule extends QuarkModule {

	public static ITag<Block> hedgesTag;
	
	@Override
	public void construct() {
		new HedgeBlock(this, Blocks.OAK_FENCE, Blocks.OAK_LEAVES);
		new HedgeBlock(this, Blocks.BIRCH_FENCE, Blocks.BIRCH_LEAVES);
		new HedgeBlock(this, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_LEAVES);
		new HedgeBlock(this, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_LEAVES);
		new HedgeBlock(this, Blocks.ACACIA_FENCE, Blocks.ACACIA_LEAVES);
		new HedgeBlock(this, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_LEAVES);
	}

	@Override
	public void modulesStarted() {
		for (BlossomTree tree : BlossomTreesModule.trees.keySet())
			new HedgeBlock(this, Blocks.SPRUCE_FENCE, tree.leaf.getBlock());
	}
	
	@Override
	public void setup() {
		hedgesTag = BlockTags.createOptional(new ResourceLocation(Quark.MOD_ID, "hedges"));
	}
	
}
