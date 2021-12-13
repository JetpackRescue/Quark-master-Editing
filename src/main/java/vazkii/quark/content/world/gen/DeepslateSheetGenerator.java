package vazkii.quark.content.world.gen;

import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraftforge.common.Tags;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.world.generator.Generator;
import vazkii.quark.content.world.module.DeepslateModule;

public class DeepslateSheetGenerator extends Generator {

	private static final long PERLIN_MAP_SEED = 185954371487L;
	
	private final PerlinNoiseGenerator noiseGenerator;
	
	public DeepslateSheetGenerator(DimensionConfig dimConfig) {
		super(dimConfig);
		
		noiseGenerator = new PerlinNoiseGenerator(new SharedSeedRandom(PERLIN_MAP_SEED), IntStream.rangeClosed(-4, 4));
	}

	@Override
	public void generateChunk(WorldGenRegion worldIn, ChunkGenerator generator, Random rand, BlockPos pos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(0, 0, 0);
		
		for(int i = 0; i < 16; i++) {
			mutable.setX(pos.getX() + i);
		
			for(int j = 0; j < 16; j++) {
				mutable.setZ(pos.getZ() + j);
				
				int start = DeepslateModule.sheetYStart; 
				int end = start + DeepslateModule.sheetHeight;
				
				double noise = noiseGenerator.noiseAt(mutable.getX(), mutable.getZ(), false);
				end += Math.round(noise * DeepslateModule.sheetHeightVariance);
				
				for(int k = start; k <= end; k++) {
					mutable.setY(k);
					
					BlockState stateAt = worldIn.getBlockState(mutable);
					boolean canPlace = true;
					if(stateAt.getBlock() == Blocks.STONE) {
						for(Direction d : Direction.values()) {
							stateAt = worldIn.getBlockState(mutable.offset(d));
							if(stateAt.getBlock().isIn(Tags.Blocks.ORES)) {
								canPlace = false;
								break;
							}
						}
						
						if(canPlace)
							worldIn.setBlockState(mutable, (k == end ? DeepslateModule.smooth_basalt : DeepslateModule.deepslate).getDefaultState(), 0);
					}
				}
			}
		}
	}

}
