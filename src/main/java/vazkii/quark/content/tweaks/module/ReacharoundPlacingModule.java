package vazkii.quark.content.tweaks.module;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.RayTraceHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class ReacharoundPlacingModule extends QuarkModule {

	@Config
	@Config.Min(0)
	@Config.Max(1)
	public double leniency = 0.5;

	@Config
	public List<String> whitelist = Lists.newArrayList();

	@Config
	public String display = "[  ]";

	@Config
	public String displayHorizontal = "<  >";

	private ReacharoundTarget currentTarget;
	private int ticksDisplayed;

	public static ITag<Item> reacharoundTag;

	@Override
	public void setup() {
		reacharoundTag = ItemTags.createOptional(new ResourceLocation(Quark.MOD_ID, "reacharound_able"));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onRender(RenderGameOverlayEvent.Pre event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
			return;

		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;

		if(player != null && currentTarget != null) {
			MainWindow res = event.getWindow();
			MatrixStack matrix = event.getMatrixStack();
			String text = (currentTarget.dir.getAxis() == Axis.Y ? display : displayHorizontal);

			matrix.push();
			matrix.translate(res.getScaledWidth() / 2F, res.getScaledHeight() / 2f - 4, 0);

			float scale = Math.min(5, ticksDisplayed + event.getPartialTicks()) / 5F;
			scale *= scale;
			int opacity = ((int) (255 * scale)) << 24;

			matrix.scale(scale, 1F, 1F);
			matrix.translate(-mc.fontRenderer.getStringWidth(text) / 2f, 0, 0);
			mc.fontRenderer.drawString(matrix, text, 0, 0, 0xFFFFFF | opacity);
			matrix.pop();
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			currentTarget = null;

			PlayerEntity player = Minecraft.getInstance().player;
			if(player != null)
				currentTarget = getPlayerReacharoundTarget(player);

			if(currentTarget != null) {
				if(ticksDisplayed < 5)
					ticksDisplayed++;
			} else ticksDisplayed = 0;
		}
	}

	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickItem event) {
		PlayerEntity player = event.getPlayer();
		ReacharoundTarget target = getPlayerReacharoundTarget(player);

		if(target != null && event.getHand() == target.hand) {
			ItemStack stack = event.getItemStack();
			if(!player.canPlayerEdit(target.pos, target.dir, stack))
				return;
			
			int count = stack.getCount();
			Hand hand = event.getHand();

			ItemUseContext context = new ItemUseContext(player, hand, new BlockRayTraceResult(new Vector3d(0.5F, 1F, 0.5F), target.dir, target.pos, false));
			boolean remote = player.world.isRemote;
			Item item = stack.getItem();
			ActionResultType res = remote ? ActionResultType.SUCCESS : item.onItemUse(context);

			if (res != ActionResultType.PASS) {
				event.setCanceled(true);
				event.setCancellationResult(res);

				if(res == ActionResultType.SUCCESS)
					player.swingArm(hand);
				else if(res == ActionResultType.CONSUME) {
					BlockPos placedPos = target.pos;
					BlockState state = player.world.getBlockState(placedPos);
					SoundType soundtype = state.getSoundType(player.world, placedPos, context.getPlayer());

					if(player.world instanceof ServerWorld)
						((ServerWorld) player.world).playSound(null, placedPos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

				}

				if(player.isCreative() && stack.getCount() < count && !remote)
					stack.setCount(count);
			}
		}
	}

	private ReacharoundTarget getPlayerReacharoundTarget(PlayerEntity player) {
		Hand hand = null;
		if(validateReacharoundStack(player.getHeldItemMainhand()))
			hand = Hand.MAIN_HAND;
		else if(validateReacharoundStack(player.getHeldItemOffhand()))
			hand = Hand.OFF_HAND;
		
		if(hand == null)
			return null;
			
		World world = player.world;

		Pair<Vector3d, Vector3d> params = RayTraceHandler.getEntityParams(player);
		double range = RayTraceHandler.getEntityRange(player);
		Vector3d rayPos = params.getLeft();
		Vector3d ray = params.getRight().scale(range);

		RayTraceResult normalRes = RayTraceHandler.rayTrace(player, world, rayPos, ray, BlockMode.OUTLINE, FluidMode.NONE);

		if (normalRes.getType() == RayTraceResult.Type.MISS) {
			ReacharoundTarget  target = getPlayerVerticalReacharoundTarget(player, hand, world, rayPos, ray);
			if(target != null)
				return target;

			target = getPlayerHorizontalReacharoundTarget(player, hand, world, rayPos, ray);
			if(target != null)
				return target;
		}

		return null;
	}

	private ReacharoundTarget getPlayerVerticalReacharoundTarget(PlayerEntity player, Hand hand, World world, Vector3d rayPos, Vector3d ray) {
		if(player.rotationPitch < 0)
			return null;

		rayPos = rayPos.add(0, leniency, 0);
		RayTraceResult take2Res = RayTraceHandler.rayTrace(player, world, rayPos, ray, BlockMode.OUTLINE, FluidMode.NONE);

		if (take2Res.getType() == RayTraceResult.Type.BLOCK && take2Res instanceof BlockRayTraceResult) {
			BlockPos pos = ((BlockRayTraceResult) take2Res).getPos().down();
			BlockState state = world.getBlockState(pos);

			if (player.getPositionVec().y - pos.getY() > 1 && (world.isAirBlock(pos) || state.getMaterial().isReplaceable()))
				return new ReacharoundTarget(pos, Direction.DOWN, hand);
		}

		return null;
	}

	private ReacharoundTarget getPlayerHorizontalReacharoundTarget(PlayerEntity player, Hand hand, World world, Vector3d rayPos, Vector3d ray) {
		Direction dir = Direction.fromAngle(player.rotationYaw);
		rayPos = rayPos.subtract(leniency * dir.getXOffset(), 0, leniency * dir.getZOffset());
		RayTraceResult take2Res = RayTraceHandler.rayTrace(player, world, rayPos, ray, BlockMode.OUTLINE, FluidMode.NONE);

		if (take2Res.getType() == RayTraceResult.Type.BLOCK && take2Res instanceof BlockRayTraceResult) {
			BlockPos pos = ((BlockRayTraceResult) take2Res).getPos().offset(dir);
			BlockState state = world.getBlockState(pos);

			if ((world.isAirBlock(pos) || state.getMaterial().isReplaceable()))
				return new ReacharoundTarget(pos, dir.getOpposite(), hand);
		}

		return null;
	}

	private boolean validateReacharoundStack(ItemStack stack) {
		Item item = stack.getItem();
		return item instanceof BlockItem || item.isIn(reacharoundTag) || whitelist.contains(Objects.toString(item.getRegistryName()));
	}
	
	private static class ReacharoundTarget {
		
		final BlockPos pos;
		final Direction dir;
		final Hand hand;
		
		public ReacharoundTarget(BlockPos pos, Direction dir, Hand hand) {
			this.pos = pos;
			this.dir = dir;
			this.hand = hand;
		}
		
	}

}
