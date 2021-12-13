package vazkii.quark.content.mobs.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.content.mobs.client.model.WraithModel;
import vazkii.quark.content.mobs.module.WraithModule;

public class WraithEntity extends ZombieEntity {

	public static final ResourceLocation LOOT_TABLE = new ResourceLocation("quark:entities/wraith");

	private static final DataParameter<String> IDLE_SOUND = EntityDataManager.createKey(WraithEntity.class, DataSerializers.STRING);
	private static final DataParameter<String> HURT_SOUND = EntityDataManager.createKey(WraithEntity.class, DataSerializers.STRING);
	private static final DataParameter<String> DEATH_SOUND = EntityDataManager.createKey(WraithEntity.class, DataSerializers.STRING);
	private static final String TAG_IDLE_SOUND = "IdleSound";
	private static final String TAG_HURT_SOUND = "HurtSound";
	private static final String TAG_DEATH_SOUND = "DeathSound";

	public WraithEntity(EntityType<? extends WraithEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerData() {
		super.registerData();

		dataManager.register(IDLE_SOUND, "");
		dataManager.register(HURT_SOUND, "");
		dataManager.register(DEATH_SOUND, "");
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MonsterEntity.func_234295_eP_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 15)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 35)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3)
				.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1)
				.createMutableAttribute(Attributes.ARMOR, 0)
				.createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0);
	}
	
	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		// NO-OP
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return getSound(IDLE_SOUND);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return getSound(HURT_SOUND);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getSound(DEATH_SOUND);
	}

	@Override
	protected float getSoundPitch() {
		return rand.nextFloat() * 0.1F + 0.75F;
	}

	public SoundEvent getSound(DataParameter<String> param) {
		ResourceLocation loc = new ResourceLocation(dataManager.get(param));
		return ForgeRegistries.SOUND_EVENTS.getValue(loc);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		AxisAlignedBB aabb = getBoundingBox();
		double x = aabb.minX + Math.random() * (aabb.maxX - aabb.minX);
		double y = aabb.minY + Math.random() * (aabb.maxY - aabb.minY);
		double z = aabb.minZ + Math.random() * (aabb.maxZ - aabb.minZ);
		getEntityWorld().addParticle(ParticleTypes.MYCELIUM, x, y, z, 0, 0, 0);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean did = super.attackEntityAsMob(entityIn);
		if(did) {
			if(entityIn instanceof LivingEntity)
				((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 1));

			double dx = getPosX() - entityIn.getPosX();
			double dz = getPosZ() - entityIn.getPosZ();
			Vector3d vec = new Vector3d(dx, 0, dz).normalize().add(0, 0.5, 0).normalize().scale(0.85);
			setMotion(vec);
		}

		return did;
	}
	
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
		int idx = rand.nextInt(WraithModule.validWraithSounds.size());
		String sound = WraithModule.validWraithSounds.get(idx);
		String[] split = sound.split("\\|");

		dataManager.set(IDLE_SOUND, split[0]);
		dataManager.set(HURT_SOUND, split[1]);
		dataManager.set(DEATH_SOUND, split[2]);
		
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	@Override
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}
	
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		compound.putString(TAG_IDLE_SOUND, dataManager.get(IDLE_SOUND));
		compound.putString(TAG_HURT_SOUND, dataManager.get(HURT_SOUND));
		compound.putString(TAG_DEATH_SOUND, dataManager.get(DEATH_SOUND));
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		dataManager.set(IDLE_SOUND, compound.getString(TAG_IDLE_SOUND));
		dataManager.set(HURT_SOUND, compound.getString(TAG_HURT_SOUND));
		dataManager.set(DEATH_SOUND, compound.getString(TAG_DEATH_SOUND));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	public void setChild(boolean childZombie) {
		// NO-OP
	}

	@Override
	public boolean isChild() {
		return false;
	}
	
	@Override
	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		BlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		return block.isIn(WraithModule.wraithSpawnableTag) ? 1F : 0F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!super.attackEntityFrom(source, amount)) {
			return false;
		} else return this.world instanceof ServerWorld;
	}

	@Override
	protected void applyAttributeBonuses(float difficulty) {}

}
