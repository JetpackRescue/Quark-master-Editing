package vazkii.quark.content.building.module;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.WoodPostBlock;

@LoadModule(category = ModuleCategory.BUILDING)
public class WoodenPostsModule extends QuarkModule {

	@Override
	public void construct() {
		ImmutableList.of(Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, 
				Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE, 
				Blocks.CRIMSON_FENCE, Blocks.WARPED_FENCE)
		.forEach(b -> {
			boolean nether = b.material == Material.NETHER_WOOD;
			WoodPostBlock post = new WoodPostBlock(this, b, "",  nether);
			post.strippedBlock = new WoodPostBlock(this, b, "stripped_", nether);
		});
	}
	
	public static boolean canLanternConnect(BlockState state, IWorldReader worldIn, BlockPos pos, boolean prev) {
		return prev || (ModuleLoader.INSTANCE.isModuleEnabled(WoodenPostsModule.class) && state.get(LanternBlock.HANGING) && worldIn.getBlockState(pos.up()).getBlock() instanceof WoodPostBlock);
	}
	
}
