package vazkii.quark.content.automation.module;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.AUTOMATION, hasSubscriptions = true)
public class JukeboxAutomationModule extends QuarkModule {

	private static final ResourceLocation JUKEBOX_ITEM_HANDLER = new ResourceLocation(Quark.MOD_ID, "jukebox_item_handler");

	@Override
	public void setup() {
		MusicDiscBehaviour behaviour = new MusicDiscBehaviour();
		ForgeRegistries.ITEMS.forEach(i -> {
			if(i instanceof MusicDiscItem)
				DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.put(i, behaviour);
		});
	}

	@SubscribeEvent
	public void attachCaps(AttachCapabilitiesEvent<TileEntity> event) {
		if(event.getObject() instanceof JukeboxTileEntity)
			event.addCapability(JUKEBOX_ITEM_HANDLER, new JukeboxItemHandler((JukeboxTileEntity) event.getObject()));
	}

	public static class JukeboxItemHandler implements ICapabilityProvider, IItemHandler {

		final JukeboxTileEntity tile;

		public JukeboxItemHandler(JukeboxTileEntity tile) {
			this.tile = tile;
		}

		@Override
		public int getSlots() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return tile.getRecord();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			ItemStack stackAt = getStackInSlot(slot);
			if(!stackAt.isEmpty()) {
				ItemStack copy = stackAt.copy();
				if(!simulate) {
		            tile.getWorld().playEvent(1010, tile.getPos(), 0);
					tile.setRecord(ItemStack.EMPTY);
					
					BlockState state = tile.getBlockState().with(JukeboxBlock.HAS_RECORD, false);
					tile.getWorld().setBlockState(tile.getPos(), state, 1|2);
				}

				return copy;
			}

			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {	
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return false;
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return LazyOptional.of(() -> this).cast();

			return LazyOptional.empty();
		}

	}

	public static class MusicDiscBehaviour extends OptionalDispenseBehavior {

		@Nonnull
		@Override
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			Direction dir = source.getBlockState().get(DispenserBlock.FACING);
			BlockPos pos = source.getBlockPos().offset(dir);
			World world = source.getWorld();
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() == Blocks.JUKEBOX) {
				JukeboxTileEntity jukebox = (JukeboxTileEntity) world.getTileEntity(pos);
				if (jukebox != null) {
					ItemStack currentRecord = jukebox.getRecord();
					((JukeboxBlock) state.getBlock()).insertRecord(world, pos, state, stack);
					world.playEvent(null, 1010, pos, Item.getIdFromItem(stack.getItem()));

					return currentRecord;
				}
			}

			return super.dispenseStack(source, stack);
		}

	}

}
