package vazkii.quark.content.world.block;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;

public class CaveCrystalClusterBlock extends QuarkBlock implements IWaterLoggable {
	
	public final CaveCrystalBlock base;
	
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public CaveCrystalClusterBlock(CaveCrystalBlock base) {
		super(base.getRegistryName().getPath() + "_cluster", base.getModule(), ItemGroup.DECORATIONS, Block.Properties.from(base));
		
		this.base = base;
		base.cluster = this;
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
		setDefaultState(getDefaultState().with(FACING, Direction.DOWN).with(WATERLOGGED, false));
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if(!isValidPosition(state, worldIn, pos))
			worldIn.destroyBlock(pos, true);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Direction dir = state.get(FACING);
		BlockPos off = pos.offset(dir.getOpposite());
		BlockState offState = worldIn.getBlockState(off);
		return offState.isSolidSide(worldIn, off, dir);
	}

	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos) {
		return VoxelShapes.empty();
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getFace()).with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	@Override
	public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
		return type == PathType.WATER && worldIn.getFluidState(pos).isTagged(FluidTags.WATER); 
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}
	
}
