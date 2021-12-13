package vazkii.quark.content.tweaks.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class InfinityBucketModule extends QuarkModule {

	private static Map<Pair<PlayerEntity, Hand>, TrackedPlayer> bukkitPlayers = new HashMap<>();

	@Config public static int cost = 10;

	@Config(description = "Set this to false to prevent dispensers from using infinite water buckets") 
	public static boolean allowDispensersToUse = true;

	@Override
	public void loadComplete() {
		if(enabled) {
			IDispenseItemBehavior behaviour = new DefaultDispenseItemBehavior() {
				private final DefaultDispenseItemBehavior field_239793_b_ = new DefaultDispenseItemBehavior();

				@Override
				public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
					boolean returnItself = false;
					if(enabled && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0) {
						if(!allowDispensersToUse)
							return field_239793_b_.dispense(source, stack);;

							returnItself = true;
					}

					ItemStack copy = stack.copy();
					BucketItem bucketitem = (BucketItem) stack.getItem();

					BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
					World world = source.getWorld();
					if(bucketitem.tryPlaceContainedLiquid(null, world, blockpos, null)) {
						bucketitem.onLiquidPlaced(world, stack, blockpos);
						return returnItself ? copy : new ItemStack(Items.BUCKET);
					} else
						return field_239793_b_.dispense(source, stack);
				}
			};

			Map<Item, IDispenseItemBehavior> registry = DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY;
			registry.put(Items.WATER_BUCKET, behaviour);
		}
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if(left.getItem() == Items.WATER_BUCKET && right.getItem() == Items.ENCHANTED_BOOK) {
			Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(right);
			if(enchs.containsKey(Enchantments.INFINITY) && enchs.get(Enchantments.INFINITY) > 0) {
				ItemStack result = left.copy();

				Map<Enchantment, Integer> map = new HashMap<>();
				map.put(Enchantments.INFINITY, 1);
				EnchantmentHelper.setEnchantments(map, result);

				event.setOutput(result);
				event.setCost(cost);
			}
		}
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if(event.phase == Phase.END)
			return;

		PlayerEntity player = event.player;
		int slot = player.inventory.currentItem;

		for(Hand hand : Hand.values()) {
			Pair<PlayerEntity, Hand> pair = Pair.of(player, hand);

			if(bukkitPlayers.containsKey(pair)) {
				TrackedPlayer tracked = bukkitPlayers.get(pair);
				ItemStack curr = player.inventory.getStackInSlot(slot);
				if(curr.getItem() == Items.BUCKET && tracked.canReplace(player)) {
					if(slot == tracked.slot || hand == Hand.OFF_HAND)
						player.setHeldItem(hand, tracked.stack);
					else 
						player.inventory.setInventorySlotContents(slot, tracked.stack);
				}

				bukkitPlayers.remove(pair);
			}
		}		
		
		for(Hand hand : Hand.values()) {
			ItemStack stack = player.getHeldItem(hand);
			if(isInfiniteBucket(stack))
				bukkitPlayers.put(Pair.of(player, hand), new TrackedPlayer(slot, player, stack.copy()));
		}
	}

	private static boolean isInfiniteBucket(ItemStack stack) {
		return stack.getItem() == Items.WATER_BUCKET && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
	}


	private static class TrackedPlayer {

		private int slot, count;
		private ItemStack stack;

		public TrackedPlayer(int slot, PlayerEntity player, ItemStack stack) {
			this.slot = slot;
			this.count = getCount(player);
			this.stack = stack;
		}

		private static int getCount(PlayerEntity player) {
			int total = 0;

			if(isInfiniteBucket(player.inventory.getItemStack()))
				total++;

			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(isInfiniteBucket(stack))
					total++;
			}

			return total;
		}

		public boolean canReplace(PlayerEntity player) {
			return this.count == (getCount(player) + 1);
		}

	}

}
