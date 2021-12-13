package vazkii.quark.content.mobs.entity;

import javax.annotation.Nonnull;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.base.handler.QuarkSounds;

public class SoulBeadEntity extends Entity {

	private static final DataParameter<Integer> TARGET_X = EntityDataManager.createKey(SoulBeadEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TARGET_Z = EntityDataManager.createKey(SoulBeadEntity.class, DataSerializers.VARINT);

	private int liveTicks = 0;
	private static final String TAG_TARGET_X = "targetX";
	private static final String TAG_TARGET_Z = "targetZ";
	
	public SoulBeadEntity(EntityType<? extends SoulBeadEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	public void setTarget(int x, int z) {
		dataManager.set(TARGET_X, x);
		dataManager.set(TARGET_Z, z);
	}

	@Override
	protected void registerData() {
		dataManager.register(TARGET_X, 0);
		dataManager.register(TARGET_Z, 0);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		double posSpread = 0.4;
		double scale = 0.08;
		double rotateSpread = 1.5;
		double rise = 0.025;
		int maxLiveTime = 6000;
		int particles = 20;
		double trigArg = liveTicks * 0.32;
		
		if((maxLiveTime - liveTicks) < particles)
			particles = (maxLiveTime - liveTicks);
		
		double posX = getPosX();
		double posY = getPosY();
		double posZ = getPosZ();
		
		Vector3d vec = new Vector3d((double) dataManager.get(TARGET_X), posY, (double) dataManager.get(TARGET_Z)).subtract(posX, posY, posZ).normalize().scale(scale);
		double bpx = posX + vec.x * liveTicks + Math.cos(trigArg) * rotateSpread;
		double bpy = posY + vec.y * liveTicks + liveTicks * rise;
		double bpz = posZ + vec.z * liveTicks + Math.sin(trigArg) * rotateSpread;
		
		for(int i = 0; i < particles; i++) {
			double px = bpx + (Math.random() - 0.5) * posSpread;
			double py = bpy + (Math.random() - 0.5) * posSpread;
			double pz = bpz + (Math.random() - 0.5) * posSpread;
			world.addParticle(new RedstoneParticleData(0.2F, 0.12F, 0.1F, 1F), px, py, pz, 0, 0, 0);
			if(Math.random() < 0.05)
				world.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, Blocks.SOUL_SAND.getDefaultState()), px, py, pz, 0, 0, 0);
		}
		
		if(Math.random() < 0.1)
			world.playSound(null, bpx, bpy, bpz, QuarkSounds.ENTITY_SOUL_BEAD_IDLE, SoundCategory.PLAYERS, 0.2F, 1F);

		liveTicks++;
		if(liveTicks > maxLiveTime)
			setDead();
	}
	
	@Override
	public void writeAdditional(@Nonnull CompoundNBT compound) {
		dataManager.set(TARGET_X, compound.getInt(TAG_TARGET_X));
		dataManager.set(TARGET_Z, compound.getInt(TAG_TARGET_Z));
	}

	@Override
	protected void readAdditional(@Nonnull CompoundNBT compound) {
		compound.putInt(TAG_TARGET_X, dataManager.get(TARGET_X));
		compound.putInt(TAG_TARGET_Z, dataManager.get(TARGET_Z));
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
