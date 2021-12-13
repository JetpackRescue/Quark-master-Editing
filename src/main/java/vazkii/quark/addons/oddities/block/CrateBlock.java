package vazkii.quark.addons.oddities.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.addons.oddities.tile.CrateTileEntity;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class CrateBlock extends QuarkBlock {

	public static final BooleanProperty PROPERTY_OPEN = BlockStateProperties.OPEN;

	public CrateBlock(QuarkModule module) {
		super("crate", module, ItemGroup.DECORATIONS, Properties.from(Blocks.BARREL));
		setDefaultState(stateContainer.getBaseState().with(PROPERTY_OPEN, false));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(worldIn.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if(tileentity instanceof CrateTileEntity) {
				if(player instanceof ServerPlayerEntity)
					NetworkHooks.openGui((ServerPlayerEntity) player, (CrateTileEntity) worldIn.getTileEntity(pos), pos);

				PiglinTasks.func_234478_a_(player, true);
			}

			return ActionResultType.CONSUME;
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if(tileentity instanceof CrateTileEntity)
				((CrateTileEntity) tileentity).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof CrateTileEntity)
			((CrateTileEntity)tileentity).crateTick();
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if(!state.isIn(newState.getBlock())) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if(tileentity instanceof CrateTileEntity) {
				CrateTileEntity crate = (CrateTileEntity) tileentity;
				crate.spillTheTea();
			}
		}

		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CrateTileEntity();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(PROPERTY_OPEN);
	}

}
