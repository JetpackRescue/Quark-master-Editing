package vazkii.quark.content.mobs.entity;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.mobs.module.ForgottenModule;
import vazkii.quark.content.tools.module.ColorRunesModule;

public class ForgottenEntity extends SkeletonEntity {

	public static final DataParameter<ItemStack> SHEATHED_ITEM = EntityDataManager.createKey(ForgottenEntity.class, DataSerializers.ITEMSTACK);

	public static final ResourceLocation FORGOTTEN_LOOT_TABLE = new ResourceLocation("quark", "entities/forgotten");

	public ForgottenEntity(EntityType<? extends ForgottenEntity> type, World world) {
		super(type, world);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(SHEATHED_ITEM, ItemStack.EMPTY);
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MonsterEntity.func_234295_eP_()
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
				.createMutableAttribute(Attributes.MAX_HEALTH, 60)
				.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1);
	}

	@Override
	@Nullable
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		ILivingEntityData ilivingentitydata = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		setCombatTask();
		
		return ilivingentitydata;
	}

	@Override
	public void tick() {
		super.tick();

		if(!world.isRemote) {
			LivingEntity target = getAttackTarget();
			boolean shouldUseBow = target == null;
			if(!shouldUseBow) {
				 EffectInstance eff = target.getActivePotionEffect(Effects.BLINDNESS);
				 shouldUseBow = eff == null || eff.getDuration() < 20;
			}
			
			boolean isUsingBow = getHeldItemMainhand().getItem() instanceof BowItem;
			if(shouldUseBow != isUsingBow)
				swap();
		}

		double w = getWidth() * 2;
		double h = getHeight();
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + Math.random() * w - w/2, getPosY() + Math.random() * h, getPosZ() + Math.random() * w - w/2, 0, 0, 0);
	}

	private void swap() {
		ItemStack curr = getHeldItemMainhand();
		ItemStack off = dataManager.get(SHEATHED_ITEM);

		setHeldItem(Hand.MAIN_HAND, off);
		dataManager.set(SHEATHED_ITEM, curr);

		Stream<PrioritizedGoal> stream = goalSelector.getRunningGoals();
		stream.map(PrioritizedGoal::getGoal)
		.filter(g -> g instanceof MeleeAttackGoal || g instanceof RangedBowAttackGoal<?>)
		.forEach(Goal::resetTask);

		setCombatTask();
	}


	@Nonnull
	@Override
	protected ResourceLocation getLootTable() {
		return FORGOTTEN_LOOT_TABLE;
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		CompoundNBT sheathed = new CompoundNBT();
		dataManager.get(SHEATHED_ITEM).write(sheathed);
		compound.put("sheathed", sheathed);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		CompoundNBT sheathed = compound.getCompound("sheathed");
		dataManager.set(SHEATHED_ITEM, ItemStack.read(sheathed));
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return 2.1F;
	}

	@Override
	protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
		// NO-OP
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		super.setEquipmentBasedOnDifficulty(difficulty);

		prepareEquipment();
	}
	
	public void prepareEquipment() {
		ItemStack bow = new ItemStack(Items.BOW);
		ItemStack sheathed = new ItemStack(Items.IRON_SWORD);

		EnchantmentHelper.addRandomEnchantment(rand, bow, 20, false);
		EnchantmentHelper.addRandomEnchantment(rand, sheathed, 20, false);

		if(ModuleLoader.INSTANCE.isModuleEnabled(ColorRunesModule.class) && rand.nextBoolean()) {
			List<Item> items = ColorRunesModule.runesLootableTag.getAllElements();
			ItemStack item = new ItemStack(items.get(rand.nextInt(items.size())));
			CompoundNBT runeNbt = item.serializeNBT();

			ItemNBTHelper.setBoolean(bow, ColorRunesModule.TAG_RUNE_ATTACHED, true);
			ItemNBTHelper.setBoolean(sheathed, ColorRunesModule.TAG_RUNE_ATTACHED, true);

			ItemNBTHelper.setCompound(bow, ColorRunesModule.TAG_RUNE_COLOR, runeNbt);
			ItemNBTHelper.setCompound(sheathed, ColorRunesModule.TAG_RUNE_COLOR, runeNbt);
		}

		setItemStackToSlot(EquipmentSlotType.MAINHAND, bow);
		dataManager.set(SHEATHED_ITEM, sheathed);

		setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(ForgottenModule.forgotten_hat));
	}

	@Override
	protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
		AbstractArrowEntity arrow = super.fireArrow(arrowStack, distanceFactor);
		if(arrow instanceof ArrowEntity) {
			ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
			PotionUtils.appendEffects(stack, ImmutableSet.of(new EffectInstance(Effects.BLINDNESS, 100, 0)));
			((ArrowEntity) arrow).setPotionEffect(stack);
		}

		return arrow;
	}
	
	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
