package vazkii.quark.content.tools.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;

public class AbacusItem extends QuarkItem {

	public static final String TAG_POS_X = "boundPosX";
	public static final String TAG_POS_Y = "boundPosY";
	public static final String TAG_POS_Z = "boundPosZ";

	public static int MAX_COUNT = 48;

	public AbacusItem(QuarkModule module) {
		super("abacus", module, new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		ItemStack stack = context.getItem();
		BlockPos curr = getBlockPos(stack);
		if(curr != null)
			setBlockPos(stack, null);
		else setBlockPos(stack, context.getPos());

		return ActionResultType.SUCCESS;
	}

	public static void setBlockPos(ItemStack stack, BlockPos pos) {
		if(pos == null)
			ItemNBTHelper.setInt(stack, TAG_POS_Y, -1);
		else {
			ItemNBTHelper.setInt(stack, TAG_POS_X, pos.getX());
			ItemNBTHelper.setInt(stack, TAG_POS_Y, pos.getY());
			ItemNBTHelper.setInt(stack, TAG_POS_Z, pos.getZ());
		}
	}

	public static BlockPos getBlockPos(ItemStack stack) {
		int y = ItemNBTHelper.getInt(stack, TAG_POS_Y, -1);
		if(y == -1)
			return null;

		int x = ItemNBTHelper.getInt(stack, TAG_POS_X, 0);
		int z = ItemNBTHelper.getInt(stack, TAG_POS_Z, 0);
		return new BlockPos(x, y, z);
	}

	public static int getCount(ItemStack stack, BlockPos target, World world) {
		BlockPos pos = getBlockPos(stack); 

		if(pos != null && !world.isAirBlock(target))
			return target.manhattanDistance(pos);

		return -1;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static int getCount(ItemStack stack, LivingEntity entityIn) {
		int count = -1;
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;

		if(entityIn == player) {
			RayTraceResult result = mc.objectMouseOver;
			if(result instanceof BlockRayTraceResult) {
				BlockPos target = ((BlockRayTraceResult) result).getPos();
				count = getCount(stack, target, player.world);
			}
		}
	
		return count;
	}

	@OnlyIn(Dist.CLIENT)
	public static float count(ItemStack stack, ClientWorld world, LivingEntity entityIn) {
		int count = getCount(stack, entityIn);
		if(count == -1)
			return 9999;

		return 0.01F * count + 0.005F;
	}

}
