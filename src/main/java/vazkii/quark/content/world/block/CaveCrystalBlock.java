package vazkii.quark.content.world.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.module.SpiralSpiresModule;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule;

/**
 * @author WireSegal
 * Created at 12:31 PM on 9/19/19.
 */
public class CaveCrystalBlock extends QuarkGlassBlock {

	public final float[] colorComponents;
	public final Vector3d colorVector;
	final boolean waxed;

	public CaveCrystalClusterBlock cluster;
	public CaveCrystalBlock alternate;

	public CaveCrystalBlock(String regname, int color, QuarkModule module, MaterialColor materialColor, boolean waxed) {
		super(regname, module, ItemGroup.DECORATIONS,
				Block.Properties.create(Material.GLASS, materialColor)
				.hardnessAndResistance(0.3F, 0F)
				.sound(SoundType.GLASS)
				.setLightLevel(b -> 11)
				.harvestTool(ToolType.PICKAXE)
				.setRequiresTool()
				.harvestLevel(0)
				.tickRandomly()
				.notSolid());

		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		colorComponents = new float[]{r, g, b};
		colorVector = new Vector3d(r, g, b);
		this.waxed = waxed;

		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.TRANSLUCENT);
	}

	private boolean canGrow(World world, BlockPos pos) {
		if(!waxed && CaveCrystalUndergroundBiomeModule.caveCrystalGrowthChance >= 1 && pos.getY() < 24 && world.isAirBlock(pos.up())) {
			int i;
			for(i = 1; world.getBlockState(pos.down(i)).getBlock() == this; ++i);

			return i < 4;
		}
		return false;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		ItemStack stack = player.getHeldItem(hand);
		if(waxed) {
			if(stack.getItem().getToolTypes(stack).contains(ToolType.AXE)) {
				if(!world.isRemote) {
					world.setBlockState(pos, alternate.getDefaultState());
					world.playEvent(2001, pos, Block.getStateId(state));
				}
				if(!player.isCreative())
					MiscUtil.damageStack(player, hand, stack, 1);
				
				return ActionResultType.SUCCESS;
			}
		} else {
			if(stack.getItem() == Items.HONEYCOMB) {
				if(!world.isRemote) {
					world.setBlockState(pos, alternate.getDefaultState());
					world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundCategory.PLAYERS, 1F, 1F);
				}
				
				if(!player.isCreative())
					stack.setCount(stack.getCount() - 1);
				
				return ActionResultType.SUCCESS;
			}
		}
		
		return ActionResultType.PASS;
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if(canGrow(worldIn, pos) && random.nextInt(CaveCrystalUndergroundBiomeModule.caveCrystalGrowthChance) == 0) {
			BlockState down = worldIn.getBlockState(pos.down());
			BlockPos up = pos.up();
			worldIn.setBlockState(up, state);

			if(down.getBlock() == SpiralSpiresModule.myalite_crystal && ModuleLoader.INSTANCE.isModuleEnabled(SpiralSpiresModule.class) && SpiralSpiresModule.renewableMyalite)
				worldIn.setBlockState(pos, SpiralSpiresModule.myalite_crystal.getDefaultState());
			else for(Direction d : Direction.values()) {
				BlockPos offPos = up.offset(d);
				if(worldIn.isAirBlock(offPos) && random.nextInt(3) == 0)
					worldIn.setBlockState(offPos, cluster.getDefaultState().with(CaveCrystalClusterBlock.FACING, d));
			}
		}
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(canGrow(worldIn, pos)) {
			double x = (double)pos.getX() + rand.nextDouble();
			double y = (double)pos.getY() + rand.nextDouble();
			double z = (double)pos.getZ() + rand.nextDouble();

			worldIn.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, x, y, z, colorComponents[0], colorComponents[1], colorComponents[2]);
		}

		if(!waxed)
			for(int i = 0; i < 4; i++) {
				double range = 5;

				double ox = rand.nextDouble() * range - (range / 2);
				double oy = rand.nextDouble() * range - (range / 2);
				double oz = rand.nextDouble() * range - (range / 2);

				double x = (double)pos.getX() + 0.5 + ox;
				double y = (double)pos.getY() + 0.5 + oy;
				double z = (double)pos.getZ() + 0.5 + oz;

				float size = 0.4F + rand.nextFloat() * 0.5F;

				if(rand.nextDouble() < 0.1) {
					double ol = ((ox * ox) + (oy * oy) + (oz * oz)) * -2;
					if(ol == 0)
						ol = 0.0001;
					worldIn.addParticle(ParticleTypes.END_ROD, x, y, z, ox / ol, oy / ol, oz / ol);
				}

				worldIn.addParticle(new RedstoneParticleData(colorComponents[0], colorComponents[1], colorComponents[2], size), x, y, z, 0, 0, 0);
			}
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
		return colorComponents;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Vector3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vector3d originalColor, float partialTicks) {
		return colorVector;
	}

}
