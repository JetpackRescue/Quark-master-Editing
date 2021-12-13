package vazkii.quark.content.building.block;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.entity.StoolEntity;
import vazkii.quark.content.building.module.StoolsModule;

public class StoolBlock extends QuarkBlock implements IWaterLoggable {

	private static final VoxelShape SHAPE_TOP = Block.makeCuboidShape(0F, 1F, 0F, 16F, 9F, 16F);
	private static final VoxelShape SHAPE_LEG = Block.makeCuboidShape(0F, 0F, 0F, 4F, 1F, 4F);

	private static final VoxelShape SHAPE_TOP_BIG = Block.makeCuboidShape(0F, 8F, 0F, 16F, 16F, 16F);
	private static final VoxelShape SHAPE_LEG_BIG = Block.makeCuboidShape(0F, 0F, 0F, 4F, 8F, 4F);

	private static final VoxelShape SHAPE = VoxelShapes.or(SHAPE_TOP, SHAPE_LEG,
			SHAPE_LEG.withOffset(0.75F, 0F, 0F),
			SHAPE_LEG.withOffset(0.75F, 0F, 0.75F),
			SHAPE_LEG.withOffset(0F, 0F, 0.75F));

	private static final VoxelShape SHAPE_BIG = VoxelShapes.or(SHAPE_TOP_BIG, SHAPE_LEG_BIG,
			SHAPE_LEG_BIG.withOffset(0.75F, 0F, 0F),
			SHAPE_LEG_BIG.withOffset(0.75F, 0F, 0.75F),
			SHAPE_LEG_BIG.withOffset(0F, 0F, 0.75F));

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty BIG = BooleanProperty.create("big");
	public static final BooleanProperty SAT_IN = BooleanProperty.create("sat_in");

	public StoolBlock(QuarkModule module, DyeColor color) {
		super(color.getTranslationKey() + "_stool", module, ItemGroup.DECORATIONS, 
				AbstractBlock.Properties.create(Material.WOOL, color.getMapColor())
				.sound(SoundType.WOOD)
				.hardnessAndResistance(0.2F)
				.notSolid());

		setDefaultState(stateContainer.getBaseState().with(WATERLOGGED, false).with(BIG, false).with(SAT_IN, false));
	}
	
	public void blockClicked(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if(!state.get(BIG)) {
			world.setBlockState(pos, state.with(BIG, true));
	         world.getPendingBlockTicks().scheduleTick(pos, this, 1);
		}
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		fixState(worldIn, pos, state);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(state.get(SAT_IN) || !worldIn.getBlockState(pos.up()).isAir() || player.getRidingEntity() != null)
			return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

		if(!worldIn.isRemote) {
			StoolEntity entity = new StoolEntity(StoolsModule.stoolEntity, worldIn);
			entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);
			
			worldIn.addEntity(entity);
			player.startRiding(entity);
			
			worldIn.setBlockState(pos, state.with(SAT_IN, true));
		}
		 
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.5F);
	}
	
	@Override
	public void onLanded(IBlockReader worldIn, Entity entityIn) {
		if(entityIn.isSuppressingBounce())
			super.onLanded(worldIn, entityIn);
		else
			this.bounceEntity(entityIn);
	}

	private void bounceEntity(Entity entity) {
		Vector3d vector3d = entity.getMotion();
		if(vector3d.y < 0.0D) {
			double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
			entity.setMotion(vector3d.x, -vector3d.y * (double)0.66F * d0, vector3d.z);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return state.get(BIG) ? SHAPE_BIG : SHAPE;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return !state.get(WATERLOGGED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getStateFor(context.getWorld(), context.getPos());
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		fixState(worldIn, pos, state);
	}
	
	private void fixState(World worldIn, BlockPos pos, BlockState state) {
		BlockState target = getStateFor(worldIn, pos);
		if(!target.equals(state))
			worldIn.setBlockState(pos, target);
	}

	private BlockState getStateFor(World world, BlockPos pos) {
		return getDefaultState()
				.with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER)
				.with(BIG, world.getBlockState(pos.up()).getShape(world, pos.up()).getStart(Axis.Y) == 0)
				.with(SAT_IN, world.getEntitiesWithinAABB(StoolEntity.class, new AxisAlignedBB(pos, pos.up()).grow(0.4), e -> e.getPosition().equals(pos)).size() > 0);
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return blockState.get(SAT_IN) ? 15 : 0;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, BIG, SAT_IN);
	}

}
