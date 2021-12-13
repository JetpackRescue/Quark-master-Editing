package vazkii.quark.content.world.block;

import java.util.Random;
import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.module.underground.GlowshroomUndergroundBiomeModule;

public class GlowshroomBlock extends MushroomBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public GlowshroomBlock(QuarkModule module) {
		super(AbstractBlock.Properties.create(Material.PLANTS, MaterialColor.CYAN)
				.doesNotBlockMovement()
				.tickRandomly()
				.zeroHardnessAndResistance()
				.sound(SoundType.PLANT)
				.setNeedsPostProcessing((s, r, p) -> true)
				.setLightLevel(b -> 14)
				.tickRandomly());

		this.module = module;
		RegistryHelper.registerBlock(this, "glowshroom");
		RegistryHelper.setCreativeTab(this, ItemGroup.DECORATIONS);

		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return blockstate.isIn(BlockTags.MUSHROOM_GROW_BLOCK) || blockstate.canSustainPlant(worldIn, blockpos, Direction.UP, this);
	}

	@Override
	public void tick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, Random rand) {
		if(rand.nextInt(GlowshroomUndergroundBiomeModule.glowshroomGrowthRate) == 0 && worldIn.getBlockState(pos.down()).getBlock() == GlowshroomUndergroundBiomeModule.glowcelium) {
			int i = 5;

			for(BlockPos targetPos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
				if(worldIn.getBlockState(targetPos).getBlock() == this) {
					--i;

					if(i <= 0)
						return;
				}
			}

			BlockPos shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

			for(int k = 0; k < 4; ++k) {
				if (worldIn.isAirBlock(shiftedPos) && state.isValidPosition(worldIn, shiftedPos))
					pos = shiftedPos;

				shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
			}

			if(worldIn.isAirBlock(shiftedPos) && state.isValidPosition(worldIn, shiftedPos))
				worldIn.setBlockState(shiftedPos, getDefaultState(), 2);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		if(rand.nextInt(20) == 0)
			worldIn.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.2 + rand.nextDouble() * 0.6, pos.getY() + 0.3, pos.getZ() + 0.2 + rand.nextDouble() * 0.6, 0, 0, 0);
	}

	@Override
	public boolean canGrow(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) {
		return GlowshroomUndergroundBiomeModule.enableHugeGlowshrooms;
	}

	@Override
	public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		return GlowshroomUndergroundBiomeModule.enableHugeGlowshrooms && rand.nextFloat() < 0.4;
	}

	@Override
	public void grow(@Nonnull ServerWorld worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		if(GlowshroomUndergroundBiomeModule.enableHugeGlowshrooms) {
			worldIn.removeBlock(pos, false);
			if(!HugeGlowshroomBlock.place(worldIn, rand, pos))
				worldIn.setBlockState(pos, getDefaultState());
		}
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}


	@Override
	public GlowshroomBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}
}
