package vazkii.quark.content.world.gen.underground;

import java.util.Random;

import it.unimi.dsi.fastutil.ints.Int2ByteArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.WorldGenRegion;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.content.world.block.CaveCrystalBlock;
import vazkii.quark.content.world.block.CaveCrystalClusterBlock;
import vazkii.quark.content.world.gen.UndergroundBiomeGenerator.Context;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule;

public class CaveCrystalUndergroundBiome extends BasicUndergroundBiome {

	public CaveCrystalUndergroundBiome() {
		super(Blocks.AIR.getDefaultState(), Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState());
	}

	private static final Int2ByteMap CRYSTAL_DATA = new Int2ByteArrayMap();

	@Override
	public void fillCeiling(Context context, BlockPos pos, BlockState state) {
		byte raw = calculateRawColorData(context.source);
		int floorIdx = raw & 0xF;
		int ceilIdx = (raw >> 4) & 0xF;
		if (ceilIdx >= floorIdx)
			ceilIdx++;

		if(context.random.nextDouble() < CaveCrystalUndergroundBiomeModule.crystalChance)
			makeCrystalIfApt(context, pos, Direction.DOWN, ceilIdx);
	}

	@Override
	public void fillFloor(Context context, BlockPos pos, BlockState state) {
		byte raw = calculateRawColorData(context.source);
		int floorIdx = raw & 0xF;

		if(context.random.nextDouble() < CaveCrystalUndergroundBiomeModule.crystalChance)
			makeCrystalIfApt(context, pos, Direction.UP, floorIdx);
	}
	
	private static void makeCrystalIfApt(Context context, BlockPos pos, Direction offset, int color) {
		BlockPos crystalPos = pos.offset(offset);
		boolean hasHorizontal = false;
		
		WorldGenRegion world = context.world;
		for(Direction dir : MiscUtil.HORIZONTALS) {
			BlockPos testPos = crystalPos.offset(dir);
			if(world.getBlockState(testPos).isSolid()) {
				hasHorizontal = true;
				break;
			}
		}
		
		if(!hasHorizontal)
			return;
		
		makeCrystalAt(context, crystalPos, offset, color, CaveCrystalUndergroundBiomeModule.crystalClusterChance);
		
		if(context.random.nextDouble() < CaveCrystalUndergroundBiomeModule.doubleCrystalChance) {
			crystalPos = crystalPos.offset(offset);
			
			if(world.isAirBlock(crystalPos))
				makeCrystalAt(context, crystalPos, offset, color, 0);
		}	
	}
	
	private static void makeCrystalAt(Context context, BlockPos crystalPos, Direction offset, int color, double clusterChance) {
		CaveCrystalBlock crystal = CaveCrystalUndergroundBiomeModule.crystals.get(color);
		CaveCrystalClusterBlock cluster = crystal.cluster;

		WorldGenRegion world = context.world;
		if(context.random.nextDouble() < clusterChance)
			world.setBlockState(crystalPos, cluster.getDefaultState().with(CaveCrystalClusterBlock.FACING, offset).with(CaveCrystalClusterBlock.WATERLOGGED, world.getFluidState(crystalPos).getFluid() == Fluids.WATER), 0);
		else {
			world.setBlockState(crystalPos, crystal.getDefaultState(), 0);
			
			for(Direction dir : Direction.values()) {
				BlockPos clusterPos = crystalPos.offset(dir);
				if(world.isAirBlock(clusterPos) && context.random.nextDouble() < CaveCrystalUndergroundBiomeModule.crystalClusterOnSidesChance)
					world.setBlockState(clusterPos, cluster.getDefaultState().with(CaveCrystalClusterBlock.FACING, dir).with(CaveCrystalClusterBlock.WATERLOGGED, world.getFluidState(clusterPos).getFluid() == Fluids.WATER), 0);
			}
		}
	}

	private static byte calculateRawColorData(BlockPos source) {
		return CRYSTAL_DATA.computeIfAbsent(source.hashCode(), (src) -> {
			Random rand = new Random(src);
			return (byte) ((rand.nextInt(8) << 4) | rand.nextInt(9));
		});
	}

}
