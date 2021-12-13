package vazkii.quark.content.world.module.underground;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BeaconTileEntity.BeamSegment;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.api.IIndirectConnector;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.QuarkInheritedPaneBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.world.block.CaveCrystalBlock;
import vazkii.quark.content.world.block.CaveCrystalClusterBlock;
import vazkii.quark.content.world.config.UndergroundBiomeConfig;
import vazkii.quark.content.world.gen.underground.CaveCrystalUndergroundBiome;

@LoadModule(category = ModuleCategory.WORLD)
public class CaveCrystalUndergroundBiomeModule extends UndergroundBiomeModule {

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double crystalChance = 0.16;

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double crystalClusterChance = 0.2;

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double crystalClusterOnSidesChance = 0.6;

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double doubleCrystalChance = 0.2;

	@Config(description = "The chance that a crystal can grow, this is on average 1 in X world ticks, set to a higher value to make them grow slower. Minimum is 1, for every tick. Set to 0 to disable growth.")
	public static int caveCrystalGrowthChance = 5;

	@Config(flag = "cave_crystal_runes")
	public static boolean crystalsCraftRunes = true;

	@Config public static boolean enableBeaconRedirection = true;
	@Config public static boolean enableCollateralMovement = true;

	public static boolean staticEnabled;

	public static List<CaveCrystalBlock> crystals = Lists.newArrayList();
	public static ITag<Block> crystalTag;

	public static Block crystal(int floorIdx) {
		return crystals.get(MathHelper.clamp(floorIdx, 0, crystals.size() - 1));
	}

	@Override
	public void construct() {
		crystal("red", 0xff0000, MaterialColor.RED);
		crystal("orange", 0xff8000, MaterialColor.ADOBE);
		crystal("yellow", 0xffff00, MaterialColor.YELLOW);
		crystal("green", 0x00ff00, MaterialColor.GREEN);
		crystal("blue", 0x00ffff, MaterialColor.LIGHT_BLUE);
		crystal("indigo", 0x0000ff, MaterialColor.BLUE);
		crystal("violet", 0xff00ff, MaterialColor.MAGENTA);
		crystal("white", 0xffffff, MaterialColor.SNOW);
		crystal("black", 0x000000, MaterialColor.BLACK);
		
		super.construct();
	}

	private void crystal(String name, int color, MaterialColor material) {
		CaveCrystalBlock crystal = new CaveCrystalBlock(name + "_crystal", color, this, material, false);
		crystals.add(crystal);
		
		CaveCrystalBlock waxed = new CaveCrystalBlock("waxed_" + name + "_crystal", color, this, material, true);
		waxed.alternate = crystal;
		crystal.alternate = waxed;

		new QuarkInheritedPaneBlock(crystal);
		CaveCrystalClusterBlock cluster = new CaveCrystalClusterBlock(crystal);
		
		ClusterConnection connection = new ClusterConnection(cluster);
		IIndirectConnector.INDIRECT_STICKY_BLOCKS.add(Pair.of(connection::isValidState, connection));
	}
	
	@Override
	public void setup() {
		super.setup();
		crystalTag = BlockTags.createOptional(new ResourceLocation(Quark.MOD_ID, "crystal"));
	}

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	@Override
	protected String getBiomeName() {
		return "crystal";
	}

	@Override
	protected UndergroundBiomeConfig getBiomeConfig() {
		return new UndergroundBiomeConfig(new CaveCrystalUndergroundBiome(), 400, true, BiomeDictionary.Type.OCEAN)
				.setDefaultSize(42, 18, 22, 8);
	}

	// The value that comes out of this is fed onto a constant for the FOR loop that
	// computes the beacon segments, so we return 0 to run that code, or MAX_VALUE to not
	public static int tickBeacon(BeaconTileEntity beacon) {
		if(!staticEnabled || !enableBeaconRedirection)
			return 0; 

		World world = beacon.getWorld();
		BlockPos beaconPos = beacon.getPos();
		BlockPos currPos = beaconPos;

		int horizontalMoves = 64;
		int targetHeight = world.getHeight(Heightmap.Type.WORLD_SURFACE, beaconPos.getX(), beaconPos.getZ());

		beacon.beamColorSegments.clear();
		boolean broke = false;
		
		float[] currColor = new float[] { 1, 1, 1 };
		ExtendedBeamSegment currSegment = new ExtendedBeamSegment(Direction.UP, Vector3i.NULL_VECTOR, currColor);

		List<BlockPos> seenPositions = new LinkedList<>();
		boolean check = true;
		boolean setColor = false;
		
		while(currPos.getY() < 256 && currPos.getY() > 0 && horizontalMoves > 0) {
			currPos = currPos.offset(currSegment.dir);
			if(currSegment.dir.getAxis().isHorizontal())
				horizontalMoves--;

			BlockState blockstate = world.getBlockState(currPos);
			Block block = blockstate.getBlock();
			float[] targetColor = blockstate.getBeaconColorMultiplier(world, currPos, beaconPos);

			if(block instanceof CaveCrystalClusterBlock) {
				Direction dir = blockstate.get(CaveCrystalClusterBlock.FACING);
				if(dir == currSegment.dir)
					currSegment.incrementHeight();
				else {
					check = true;
					beacon.beamColorSegments.add(currSegment);
					
					targetColor = ((CaveCrystalClusterBlock) block).base.colorComponents;
					if(targetColor[0] == 1F && targetColor[1] == 1F && targetColor[2] == 1F)
						targetColor = currColor;
					
					float[] mixedColor = new float[]{(currColor[0] + targetColor[0] * 3) / 4.0F, (currColor[1] + targetColor[1] * 3) / 4.0F, (currColor[2] + targetColor[2] * 3) / 4.0F};
					currColor = mixedColor;
					currSegment = new ExtendedBeamSegment(dir, currPos.subtract(beaconPos), currColor);
				}
			} else if(targetColor != null) {
				if(Arrays.equals(targetColor, currColor))
					currSegment.incrementHeight();
				else {
					check = true;
					beacon.beamColorSegments.add(currSegment);

					float[] mixedColor = new float[]{(currColor[0] + targetColor[0]) / 2.0F, (currColor[1] + targetColor[1]) / 2.0F, (currColor[2] + targetColor[2]) / 2.0F};
					
					if(!setColor) {
						mixedColor = targetColor;
						setColor = true;
					}
					
					currColor = mixedColor;
					currSegment = new ExtendedBeamSegment(currSegment.dir, currPos.subtract(beaconPos), mixedColor);
				}
			} else {
				if (blockstate.getOpacity(world, currPos) >= 15 || block == Blocks.BEDROCK) {
					broke = true;
					break;
				}

				currSegment.incrementHeight();
			}
			
			if(check) {
				if(seenPositions.contains(currPos)) {
					broke = true;
					break;
				} else seenPositions.add(currPos);
			}
		}
		
		if(horizontalMoves == 0 || currPos.getY() <= 0)
			broke = true;

		if(!broke) {
			beacon.beamColorSegments.add(currSegment);
			beacon.beaconSize = targetHeight + 1;
		} else {
			beacon.beamColorSegments.clear();
			beacon.beaconSize = targetHeight;
		}
		

		return Integer.MAX_VALUE;
	}

	public static class ExtendedBeamSegment extends BeamSegment {

		public final Direction dir;
		public final Vector3i offset;
		
		private boolean isTurn = false;

		public ExtendedBeamSegment(Direction dir, Vector3i offset, float[] colorsIn) {
			super(colorsIn);
			this.offset = offset;
			this.dir = dir;
		}

		public void makeTurn() {
			isTurn = true;
		}
		
		public boolean isTurn() {
			return isTurn;
		}
		
		@Override
		public void incrementHeight() { // increase visibility
			super.incrementHeight();
		}

	}

	public static class ClusterConnection implements IIndirectConnector {

		final CaveCrystalClusterBlock cluster;
		
		public ClusterConnection(CaveCrystalClusterBlock cluster) {
			this.cluster = cluster;
		}
		
		@Override
		public boolean isEnabled() {
			return enableCollateralMovement;
		}
		
		private boolean isValidState(BlockState state) {
			return state.getBlock() == cluster; 
		}
		
		@Override
		public boolean canConnectIndirectly(World world, BlockPos ourPos, BlockPos sourcePos, BlockState ourState, BlockState sourceState) {
			BlockPos offsetPos = ourPos.offset(ourState.get(CaveCrystalClusterBlock.FACING).getOpposite());
			if(!offsetPos.equals(sourcePos))
				return false;
			
			return sourceState.getBlock() == cluster.base;
		}
		
	}
	
}
