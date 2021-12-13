package vazkii.quark.content.building.block;

import java.util.function.BooleanSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.module.HedgesModule;
import vazkii.quark.content.world.block.BlossomLeavesBlock;

public class HedgeBlock extends FenceBlock implements IQuarkBlock, IBlockColorProvider {

	private final QuarkModule module;
	final Block leaf;
	private BooleanSupplier enabledSupplier = () -> true;

	public static final BooleanProperty EXTEND = BooleanProperty.create("extend");

	public HedgeBlock(QuarkModule module, Block fence, Block leaf) {
		super(Block.Properties.from(fence));

		this.module = module;
		this.leaf = leaf;

		if (leaf instanceof BlossomLeavesBlock) {
			String colorName = leaf.getRegistryName().getPath().replaceAll("_blossom_leaves", "");
  		RegistryHelper.registerBlock(this, colorName + "_blossom_hedge");
		} else {
			RegistryHelper.registerBlock(this, fence.getRegistryName().getPath().replaceAll("_fence", "_hedge"));
		}
		
		RegistryHelper.setCreativeTab(this, ItemGroup.DECORATIONS);

		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);

		setDefaultState(getDefaultState().with(EXTEND, false));
	}
	
	@Override
	public boolean canConnect(BlockState state, boolean isSideSolid, Direction direction) {
		return state.getBlock().isIn(HedgesModule.hedgesTag);
	}
	
	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
		return facing == Direction.UP && !state.get(WATERLOGGED) && plantable.getPlantType(world, pos) == PlantType.PLAINS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IBlockReader iblockreader = context.getWorld();
		BlockPos blockpos = context.getPos();
		BlockPos down = blockpos.down();
		BlockState downState = iblockreader.getBlockState(down);

		return super.getStateForPlacement(context)
				.with(EXTEND, downState.getBlock() instanceof HedgeBlock);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		if(facing == Direction.DOWN)
			return stateIn.with(EXTEND, facingState.getBlock() instanceof HedgeBlock);
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(EXTEND);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBlockColor getBlockColor() {
		final BlockColors colors = Minecraft.getInstance().getBlockColors();
		final BlockState leafState = leaf.getDefaultState();
		return (state, world, pos, tintIndex) -> colors.getColor(leafState, world, pos, tintIndex);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IItemColor getItemColor() {
		final ItemColors colors = Minecraft.getInstance().getItemColors();
		final ItemStack leafStack = new ItemStack(leaf);
		return (stack, tintIndex) -> colors.getColor(leafStack, tintIndex);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public HedgeBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
