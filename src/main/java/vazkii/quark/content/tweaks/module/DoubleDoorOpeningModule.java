package vazkii.quark.content.tweaks.module;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.DoubleDoorMessage;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class DoubleDoorOpeningModule extends QuarkModule {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		if(!event.getWorld().isRemote || event.getPlayer().isDiscrete() || event.isCanceled() || event.getResult() == Result.DENY || event.getUseBlock() == Result.DENY)
			return;

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		
		if(world.getBlockState(pos).getBlock() instanceof DoorBlock) {
			openDoor(world, event.getPlayer(), pos);
			QuarkNetwork.sendToServer(new DoubleDoorMessage(pos));
		}
	}
	
	public static void openDoor(World world, PlayerEntity player, BlockPos pos) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(DoubleDoorOpeningModule.class) || world == null)
			return;
		
		BlockState state = world.getBlockState(pos);
		Direction direction = state.get(DoorBlock.FACING);
		boolean isOpen = state.get(DoorBlock.OPEN);
		DoorHingeSide isMirrored = state.get(DoorBlock.HINGE);

		BlockPos mirrorPos = pos.offset(isMirrored == DoorHingeSide.RIGHT ? direction.rotateYCCW() : direction.rotateY());
		BlockPos doorPos = state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? mirrorPos : mirrorPos.down();
		BlockState other = world.getBlockState(doorPos);

		if(state.getMaterial() != Material.IRON && other.getBlock() == state.getBlock() && other.get(DoorBlock.FACING) == direction && other.get(DoorBlock.OPEN) == isOpen && other.get(DoorBlock.HINGE) != isMirrored) {
			RayTraceResult res = new BlockRayTraceResult(new Vector3d(doorPos.getX() + 0.5, doorPos.getY() + 0.5, doorPos.getZ() + 0.5), direction, doorPos, false);
			if(res instanceof BlockRayTraceResult)
				other.onBlockActivated(world, player, Hand.MAIN_HAND, (BlockRayTraceResult) res);
		}
	}
	
}
