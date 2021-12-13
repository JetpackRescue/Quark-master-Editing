package vazkii.quark.content.experimental.pallet;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class PalletBlock extends QuarkBlock {

	protected static final VoxelShape AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);

	public PalletBlock(QuarkModule module) {
		super("pallet", module, ItemGroup.DECORATIONS, Properties.from(Blocks.OAK_PRESSURE_PLATE));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof PalletTileEntity && !worldIn.isRemote && handIn == Hand.MAIN_HAND) {
			PalletTileEntity pallet = (PalletTileEntity) tile;
			
			String s = String.format("%d x [%s] max=%d/%d stack", pallet.count, pallet.stack, pallet.maxAcceptedCount, (pallet.maxAcceptedCount / 64));
			player.sendMessage(new StringTextComponent(s), new UUID(12, 12));
			
			s = String.format("Inv = {%s,%s}", pallet.getStackInSlot(0), pallet.getStackInSlot(1));
			player.sendMessage(new StringTextComponent(s), new UUID(12, 12));
			
			pallet.sync();
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return AABB;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PalletTileEntity();
	}
	
}
