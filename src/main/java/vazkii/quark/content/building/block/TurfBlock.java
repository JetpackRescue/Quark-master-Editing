package vazkii.quark.content.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkSlabBlock;
import vazkii.quark.base.block.QuarkStairsBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.module.VerticalSlabsModule.IVerticalSlabProvider;

/**
 * @author WireSegal
 * Created at 11:23 AM on 10/4/19.
 */
public class TurfBlock extends QuarkBlock implements IBlockColorProvider {

	public TurfBlock(String regname, QuarkModule module, ItemGroup creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction dir, IPlantable plant) {
		return canSustain(state, world, pos, dir, plant);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBlockColor getBlockColor() {
		final BlockColors colors = Minecraft.getInstance().getBlockColors();
		final BlockState grass = Blocks.GRASS_BLOCK.getDefaultState();
		return (state, world, pos, tintIndex) -> colors.getColor(grass, world, pos, tintIndex);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IItemColor getItemColor() {
		final ItemColors colors = Minecraft.getInstance().getItemColors();
		final ItemStack grass = new ItemStack(Items.GRASS_BLOCK);
		return (stack, tintIndex) -> colors.getColor(grass, tintIndex);
	}

	public static boolean canSustain(BlockState state, IBlockReader world, BlockPos pos, Direction dir, IPlantable plant) {
		if(!state.isSolidSide(world, pos, dir))
			return false;
		
		PlantType type = plant.getPlantType(world, pos.offset(dir));
		if(plant instanceof BushBlock || PlantType.PLAINS.equals(type))
			return true;

		if(PlantType.BEACH.equals(type)) {
			boolean hasWater = world.getFluidState(pos).isTagged(FluidTags.WATER);
			
			if(!hasWater)
				for(Direction face : Direction.Plane.HORIZONTAL) {
					BlockState blockState = world.getBlockState(pos.offset(face));
					FluidState fluidState = world.getFluidState(pos.offset(face));
					hasWater |= blockState.isIn(Blocks.FROSTED_ICE);
					hasWater |= fluidState.isTagged(FluidTags.WATER);
					if(hasWater)
						break; 
				}

			return hasWater;
		}
		
		return false;
	}

	public static class TurfSlabBlock extends QuarkSlabBlock implements IVerticalSlabProvider {

		public TurfSlabBlock(IQuarkBlock parent) {
			super(parent);
		}

		@Override
		public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction dir, IPlantable plant) {
			return canSustain(state, world, pos, dir, plant);
		}

		@Override
		public VerticalSlabBlock getVerticalSlab(Block block, QuarkModule module) {
			return new TurfVerticalSlabBlock(block, module);
		}

	}

	public static class TurfStairsBlock extends QuarkStairsBlock {

		public TurfStairsBlock(IQuarkBlock parent) {
			super(parent);
		}

		@Override
		public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction dir, IPlantable plant) {
			return canSustain(state, world, pos, dir, plant);
		}

	}
	
	public static class TurfVerticalSlabBlock extends VerticalSlabBlock {

		public TurfVerticalSlabBlock(Block parent, QuarkModule module) {
			super(parent, module);
		}
		
		@Override
		public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction dir, IPlantable plant) {
			return canSustain(state, world, pos, dir, plant);
		}
		
	}

}
