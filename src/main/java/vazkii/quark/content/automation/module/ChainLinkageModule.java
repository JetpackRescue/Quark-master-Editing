/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 17, 2019, 12:37 AM (EST)]
 */
package vazkii.quark.content.automation.module;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.SyncChainMessage;
import vazkii.quark.content.automation.base.ChainHandler;
import vazkii.quark.content.automation.client.render.ChainRenderer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@LoadModule(category = ModuleCategory.AUTOMATION, hasSubscriptions = true)
public class ChainLinkageModule extends QuarkModule {

    @Config(description = "Can vehicle-linking chains be used for crafting chain armor?", flag = "chain_craft_armor")
    public static boolean craftsArmor = true;

    private static final IntObjectMap<UUID> AWAIT_MAP = new IntObjectHashMap<>();

    @Override
    public void setup() {
        if (!enabled)
            return;

        DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.put(Items.CHAIN, new LinkCartsBehaviour());
    }

    public static void queueChainUpdate(int vehicle, UUID other) {
        if (other != null && !other.equals(SyncChainMessage.NULL_UUID))
            AWAIT_MAP.put(vehicle, other);
    }

    public static void onEntityUpdate(Entity vehicle) {
        if (ChainHandler.canBeLinkedTo(vehicle))
            ChainHandler.adjustVehicle(vehicle);
    }

    public static void drop(Entity vehicle) {
        if (ChainHandler.getLinked(vehicle) != null)
            vehicle.entityDropItem(new ItemStack(Items.CHAIN), 0f);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();
        ItemStack stack = event.getItemStack();

        Entity entity = event.getTarget();

        Entity link = ChainHandler.getLinked(entity);

        boolean sneaking = player.isDiscrete();

        List<Entity> linkedToPlayer = new ArrayList<>();

        for (Entity linkCandidate : entity.world.getEntitiesWithinAABB(Entity.class, player.getBoundingBox().grow(ChainHandler.MAX_DISTANCE))) {
            if (ChainHandler.getLinked(linkCandidate) == player)
                linkedToPlayer.add(linkCandidate);
        }

        if (ChainHandler.canBeLinked(entity) && linkedToPlayer.isEmpty() && !stack.isEmpty() && stack.getItem() == Items.CHAIN && link == null) {
            if (!entity.world.isRemote) {
                ChainHandler.setLink(entity, player.getUniqueID(), true);
                if (!player.isCreative())
                    stack.shrink(1);
            }

            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        } else if (link == player) {
//            if (!entity.world.isRemote) {
//                if (!player.isCreative())
//                    entity.entityDropItem(new ItemStack(chain), 0f);
//                ChainHandler.setLink(entity, null, true);
//            }

            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        } else if (ChainHandler.canBeLinked(entity) && !linkedToPlayer.isEmpty()) {
            if (!entity.world.isRemote) {
                for (Entity linked : linkedToPlayer)
                    ChainHandler.setLink(linked, entity.getUniqueID(), true);
            }

            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        } else if (link != null && sneaking) {
            if (!entity.world.isRemote) {
                if (!player.isCreative())
                    entity.entityDropItem(new ItemStack(Items.CHAIN), 0f);
                ChainHandler.setLink(entity, null, true);
            }

            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientUpdateTick(TickEvent.ClientTickEvent event) {
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.START)
            ChainRenderer.updateTick();
    }

    @SubscribeEvent
    public void onVehicleSeen(PlayerEvent.StartTracking event) {
        if (ChainHandler.canBeLinked(event.getTarget()) && event.getPlayer() instanceof ServerPlayerEntity)
            QuarkNetwork.sendToPlayer(new SyncChainMessage(event.getTarget().getEntityId(), ChainHandler.getLink(event.getTarget())),
                    (ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public void onVehicleArrive(EntityJoinWorldEvent event) {
        Entity target = event.getEntity();
        if (event.getWorld().isRemote && ChainHandler.canBeLinked(target)) {
            int id = target.getEntityId();
            if (AWAIT_MAP.containsKey(id))
                target.getPersistentData().putUniqueId(ChainHandler.LINKED_TO, AWAIT_MAP.get(id));
            AWAIT_MAP.remove(id);
        }
    }

    public static class LinkCartsBehaviour extends OptionalDispenseBehavior {

        @Nonnull
        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            // Resolve direction the dispenser block is facing
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            // Create the area for the block in front of the dispenser
            AxisAlignedBB targetArea = new AxisAlignedBB(source.getBlockPos().offset(direction));

            // Get a list of minecarts in front of the dispenser that are not linked to anything
            List<AbstractMinecartEntity> linkableCarts = source
                .getWorld()
                .getEntitiesWithinAABB(AbstractMinecartEntity.class, targetArea)
                .stream()
                .filter(e -> ChainHandler.getLinked(e) == null)
                .collect(Collectors.toList());

            // Make sure that there are exactly two carts in the target area
            // Do nothing if there are no link-able carts, or if linking targets are ambiguous
            if (linkableCarts.size() != 2)
                return stack;

            // Link the two minecarts together
            ChainHandler.setLink(linkableCarts.get(0), linkableCarts.get(1).getUniqueID(), true);
            // Use up one of the chains during linking
            stack.shrink(1);
            // Mark the dispense result as a success
            successful = true;

            return stack;
        }
    }
}
