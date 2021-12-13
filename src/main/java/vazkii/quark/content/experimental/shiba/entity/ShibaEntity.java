package vazkii.quark.content.experimental.shiba.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.content.experimental.module.ShibaModule;
import vazkii.quark.content.experimental.shiba.ai.DeliverFetchedItemGoal;
import vazkii.quark.content.experimental.shiba.ai.FetchArrowGoal;
import vazkii.quark.content.tweaks.ai.NuzzleGoal;
import vazkii.quark.content.tweaks.ai.WantLoveGoal;

public class ShibaEntity extends TameableEntity {

	private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(ShibaEntity.class, DataSerializers.VARINT);
	private static final DataParameter<ItemStack> MOUTH_ITEM = EntityDataManager.createKey(ShibaEntity.class, DataSerializers.ITEMSTACK);
	private static final DataParameter<Integer> FETCHING = EntityDataManager.createKey(ShibaEntity.class, DataSerializers.VARINT);

	public ShibaEntity(EntityType<? extends ShibaEntity> type, World worldIn) {
		super(type, worldIn);
		setTamed(false);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(2, new SitGoal(this));
		goalSelector.addGoal(3, new FetchArrowGoal(this));
		goalSelector.addGoal(4, new DeliverFetchedItemGoal(this, 1.1D, -1F, 32.0F, false));
		goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		goalSelector.addGoal(6, new TemptGoal(this, 1, Ingredient.fromItems(Items.BONE), false));
		goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
		goalSelector.addGoal(8, new NuzzleGoal(this, 0.5F, 16, 2, SoundEvents.ENTITY_WOLF_WHINE));
		goalSelector.addGoal(9, new WantLoveGoal(this, 0.2F));
		goalSelector.addGoal(10, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(12, new LookRandomlyGoal(this));
	}

	@Override
	public void tick() {
		super.tick();

		AbstractArrowEntity fetching = getFetching();
		if(fetching != null && (isSleeping() || fetching.world != world || !fetching.isAlive() || fetching.pickupStatus == PickupStatus.DISALLOWED))
			setFetching(null);

		if(!isSleeping() && !world.isRemote && fetching == null && getMouthItem().isEmpty()) {
			LivingEntity owner = getOwner();
			if(owner != null) {
				AxisAlignedBB check = owner.getBoundingBox().grow(2);
				List<AbstractArrowEntity> arrows = world.getEntitiesWithinAABB(AbstractArrowEntity.class, check, 
						a -> a.func_234616_v_() == owner && a.pickupStatus != PickupStatus.DISALLOWED);

				if(arrows.size() > 0) {
					AbstractArrowEntity arrow = arrows.get(world.rand.nextInt(arrows.size()));
					setFetching(arrow);
				}
			}
		}
	}

	public AbstractArrowEntity getFetching() {
		int id = dataManager.get(FETCHING);
		if(id == -1)
			return null;

		Entity e = world.getEntityByID(id);
		if(e == null || !(e instanceof AbstractArrowEntity))
			return null;

		return (AbstractArrowEntity) e;
	}

	public void setFetching(AbstractArrowEntity e) {
		dataManager.set(FETCHING, e == null ? -1 : e.getEntityId());
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		Item item = stack.getItem();
		return item.isFood() && item.getFood().isMeat();
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(COLLAR_COLOR, DyeColor.RED.getId());
		dataManager.register(MOUTH_ITEM, ItemStack.EMPTY);
		dataManager.register(FETCHING, -1);
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.dataManager.get(COLLAR_COLOR));
	}

	public void setCollarColor(DyeColor collarcolor) {
		this.dataManager.set(COLLAR_COLOR, collarcolor.getId());
	}

	public ItemStack getMouthItem() {
		return dataManager.get(MOUTH_ITEM);
	}

	public void setMouthItem(ItemStack stack) {
		this.dataManager.set(MOUTH_ITEM, stack);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 8;
	}

	@Override
	public boolean canMateWith(AnimalEntity otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(otherAnimal instanceof ShibaEntity)) {
			return false;
		} else {
			ShibaEntity wolfentity = (ShibaEntity) otherAnimal;
			if (!wolfentity.isTamed()) {
				return false;
			} else if (wolfentity.isSleeping()) {
				return false;
			} else {
				return this.isInLove() && wolfentity.isInLove();
			}
		}
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("CollarColor", (byte)this.getCollarColor().getId());

		CompoundNBT itemcmp = new CompoundNBT();
		ItemStack holding = getMouthItem();
		if(!holding.isEmpty())
			holding.write(itemcmp);
		compound.put("MouthItem", itemcmp);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("CollarColor", 99))
			this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));

		if(compound.contains("MouthItem")) {
			CompoundNBT itemcmp = compound.getCompound("MouthItem");
			setMouthItem(ItemStack.read(itemcmp));
		}
	}

	@Override
	public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		if(player.isDiscrete() && player.getHeldItemMainhand().isEmpty()) {
			if(hand == Hand.MAIN_HAND && WantLoveGoal.canPet(this)) {
				if(player.world instanceof ServerWorld) {
					Vector3d pos = getPositionVec();
					((ServerWorld) player.world).spawnParticle(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0, 0, 0, 0.1);
					playSound(SoundEvents.ENTITY_WOLF_WHINE, 0.6F, 0.5F + (float) Math.random() * 0.5F);
				} else player.swingArm(Hand.MAIN_HAND);

				WantLoveGoal.setPetTime(this);
			}

			return ActionResultType.SUCCESS;
		} else
			if (this.world.isRemote) {
				boolean flag = this.isOwner(player) || this.isTamed() || item == Items.BONE && !this.isTamed();
				return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
			} else {
				if (this.isTamed()) {
					ItemStack mouthItem = getMouthItem();
					if(!mouthItem.isEmpty()) {
						ItemStack copy = mouthItem.copy();
						if(!player.addItemStackToInventory(copy))
							entityDropItem(copy);

						if(player.world instanceof ServerWorld) {
							Vector3d pos = getPositionVec();
							((ServerWorld) player.world).spawnParticle(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0, 0, 0, 0.1);
							playSound(SoundEvents.ENTITY_WOLF_WHINE, 0.6F, 0.5F + (float) Math.random() * 0.5F);
						}
						setMouthItem(ItemStack.EMPTY);
						return ActionResultType.SUCCESS;
					}

					if (this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
						if (!player.abilities.isCreativeMode) {
							itemstack.shrink(1);
						}

						this.heal((float)item.getFood().getHealing());
						return ActionResultType.SUCCESS;
					}

					if (!(item instanceof DyeItem)) {
						if(!itemstack.isEmpty() && mouthItem.isEmpty() && itemstack.getItem() instanceof SwordItem) {
							ItemStack copy = itemstack.copy();
							copy.setCount(1);
							itemstack.setCount(itemstack.getCount() - 1);

							setMouthItem(copy);
							return ActionResultType.SUCCESS;
						}

						ActionResultType actionresulttype = super.func_230254_b_(player, hand);
						if ((!actionresulttype.isSuccessOrConsume() || this.isChild()) && this.isOwner(player)) {
							this.func_233687_w_(!this.isSitting());
							this.isJumping = false;
							this.navigator.clearPath();
							this.setAttackTarget((LivingEntity)null);
							return ActionResultType.SUCCESS;
						}

						return actionresulttype;
					}

					DyeColor dyecolor = ((DyeItem)item).getDyeColor();
					if (dyecolor != this.getCollarColor()) {
						this.setCollarColor(dyecolor);
						if (!player.abilities.isCreativeMode) {
							itemstack.shrink(1);
						}

						return ActionResultType.SUCCESS;
					}
				} else if (item == Items.BONE) {
					if (!player.abilities.isCreativeMode) {
						itemstack.shrink(1);
					}

					if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
						WantLoveGoal.setPetTime(this);

						this.setTamedBy(player);
						this.navigator.clearPath();
						this.setAttackTarget((LivingEntity)null);
						this.func_233687_w_(true);
						this.world.setEntityState(this, (byte)7);
					} else {
						this.world.setEntityState(this, (byte)6);
					}

					return ActionResultType.SUCCESS;
				}

				return super.func_230254_b_(player, hand);
			}
	}

	@Override
	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
		if(tamed) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
			setHealth(20);
		} getAttribute(Attributes.MAX_HEALTH).setBaseValue(8);

		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(rand.nextInt(3) == 0)
			return getHealth() < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
		else
			return SoundEvents.ENTITY_WOLF_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_WOLF_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WOLF_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override // make baby
	public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity mate) {
		ShibaEntity wolfentity = ShibaModule.shibaType.create(world);
		UUID uuid = this.getOwnerId();
		if (uuid != null) {
			wolfentity.setOwnerId(uuid);
			wolfentity.setTamed(true);
		}

		return wolfentity;
	}

}
