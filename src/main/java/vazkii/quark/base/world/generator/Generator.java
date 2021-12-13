package vazkii.quark.base.world.generator;

import java.util.Random;
import java.util.function.BooleanSupplier;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.WorldGenRegion;
import vazkii.quark.base.module.config.type.DimensionConfig;

public abstract class Generator implements IGenerator {
	
	public static final BooleanSupplier NO_COND = () -> true;
	
	public final DimensionConfig dimConfig;
	private final BooleanSupplier condition;
	
	public Generator(DimensionConfig dimConfig) {
		this(dimConfig, NO_COND);
	}
	
	public Generator(DimensionConfig dimConfig, BooleanSupplier condition) {
		this.dimConfig = dimConfig;
		this.condition = condition;
	}

	@Override
	public final int generate(int seedIncrement, long seed, GenerationStage.Decoration stage, WorldGenRegion worldIn, ChunkGenerator generator, SharedSeedRandom rand, BlockPos pos) {
		rand.setFeatureSeed(seed, seedIncrement, stage.ordinal());
		generateChunk(worldIn, generator, rand, pos);
		return seedIncrement + 1;
	}

	public abstract void generateChunk(WorldGenRegion worldIn, ChunkGenerator generator, Random rand, BlockPos pos);

	@Override
	public boolean canGenerate(IServerWorld world) {
		return condition.getAsBoolean() && dimConfig.canSpawnHere(world.getWorld());
	}
	
	public Biome getBiome(IWorld world, BlockPos pos, boolean offset) {
		// Move the position over to the top of the world to ensure it doesn't clip into potential
		// mod-added underground biomes
		
		BlockPos testPos = offset ? new BlockPos(pos.getX(), world.getHeight() - 1, pos.getZ()) : pos;
		return world.getBiomeManager().getBiome(testPos);
	}
	
	protected boolean isNether(IWorld world) {
		return world.getDimensionType().isUltrawarm();
	}
	
}
