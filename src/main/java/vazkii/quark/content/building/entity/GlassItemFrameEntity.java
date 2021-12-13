package vazkii.quark.content.building.entity;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import vazkii.quark.base.util.MovableFakePlayer;
import vazkii.quark.content.building.module.ItemFramesModule;

public class GlassItemFrameEntity extends ItemFrameEntity implements IEntityAdditionalSpawnData {

	public static final DataParameter<Boolean> IS_SHINY = EntityDataManager.createKey(GlassItemFrameEntity.class, DataSerializers.BOOLEAN);
	
	private static final String TAG_SHINY = "isShiny";
	
	private boolean didHackery = false;
	private FakePlayer fakePlayer = null;

	public GlassItemFrameEntity(EntityType<? extends GlassItemFrameEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public GlassItemFrameEntity(World worldIn, BlockPos blockPos, Direction face) {
		super(ItemFramesModule.glassFrameEntity, worldIn);
		hangingPosition = blockPos;
		this.updateFacingWithBoundingBox(face);
	}
	
	@Override
	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
		ItemStack item = getDisplayedItem();
		if(!player.isSneaking() && !item.isEmpty() && !(item.getItem() instanceof BannerItem)) {
			BlockPos behind = getBehindPos();
			TileEntity tile = world.getTileEntity(behind);
			
			if(tile != null && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
				BlockState behindState = world.getBlockState(behind);
				ActionResultType result = behindState.onBlockActivated(world, player, hand, new BlockRayTraceResult(new Vector3d(getPosX(), getPosY(), getPosZ()), facingDirection, behind, true));
				
				if(result.isSuccessOrConsume())
					return result;
			}
		}
		
		return super.processInitialInteract(player, hand);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if(ItemFramesModule.glassItemFramesUpdateMaps) {
			ItemStack stack = getDisplayedItem();
			if(stack.getItem() instanceof FilledMapItem && world instanceof ServerWorld) {
				ServerWorld sworld = (ServerWorld) world;
				ItemStack clone = stack.copy();

				MapData data = FilledMapItem.getMapData(clone, world);
				if(data != null && !data.locked) {
					if(fakePlayer == null)
						fakePlayer = new MovableFakePlayer(sworld, new GameProfile(UUID.randomUUID(), "ItemFrame"));
					
					FilledMapItem item = (FilledMapItem) stack.getItem();
					
					clone.setAttachedEntity(null);
					fakePlayer.setPosition(getPosX(), getPosY(), getPosZ());
					fakePlayer.inventory.setInventorySlotContents(0, clone);
					
					item.updateMapData(world, fakePlayer, data);
				}
			}
		}
	}

	@Override
	protected void registerData() {
		super.registerData();
		
		dataManager.register(IS_SHINY, false);
	}
	
	@Override
	public boolean onValidSurface() {
		return super.onValidSurface() || isOnSign();
	}

	public BlockPos getBehindPos() {
		return hangingPosition.offset(facingDirection.getOpposite());
	}
	
	public boolean isOnSign() {
		BlockState blockstate = world.getBlockState(getBehindPos());
		return blockstate.getBlock().isIn(BlockTags.STANDING_SIGNS);
	}

	@Nullable
	@Override
	public ItemEntity entityDropItem(@Nonnull ItemStack stack, float offset) {
		if (stack.getItem() == Items.ITEM_FRAME && !didHackery) {
			stack = new ItemStack(getItem());
			didHackery = true;
		}

		return super.entityDropItem(stack, offset);
	}

	@Nonnull
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		ItemStack held = getDisplayedItem();
		if (held.isEmpty())
			return new ItemStack(getItem());
		else
			return held.copy();
	}
	
	private Item getItem() {
		return dataManager.get(IS_SHINY) ? ItemFramesModule.glowingGlassFrame : ItemFramesModule.glassFrame;
	}
	
	@Override
	public void writeAdditional(CompoundNBT cmp) {
		super.writeAdditional(cmp);
		
		cmp.putBoolean(TAG_SHINY, dataManager.get(IS_SHINY));
	}
	
	@Override
	public void readAdditional(CompoundNBT cmp) {
		super.readAdditional(cmp);
		
		dataManager.set(IS_SHINY, cmp.getBoolean(TAG_SHINY));
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeBlockPos(this.hangingPosition);
		buffer.writeVarInt(this.facingDirection.getIndex());
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		this.hangingPosition = buffer.readBlockPos();
		this.updateFacingWithBoundingBox(Direction.byIndex(buffer.readVarInt()));
	}
}
