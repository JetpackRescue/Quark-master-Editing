package vazkii.quark.content.building.entity;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.content.building.block.StoolBlock;

public class StoolEntity extends Entity {

	public StoolEntity(EntityType<?> entityTypeIn, World worldIn) {
		super(entityTypeIn, worldIn);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		List<Entity> passengers = getPassengers();
		boolean dead = passengers.isEmpty();
		
		BlockPos pos = getPosition();
		BlockState state = world.getBlockState(pos);
		
		if(!dead) {
			if(!(state.getBlock() instanceof StoolBlock)) {
				PistonTileEntity piston = null;
				boolean didOffset = false;
				
				TileEntity tile = world.getTileEntity(pos);
				if(tile instanceof PistonTileEntity && ((PistonTileEntity) tile).getPistonState().getBlock() instanceof StoolBlock)
					piston = (PistonTileEntity) tile;
				else for(Direction d : Direction.values()) {
					BlockPos offPos = pos.offset(d);
					tile = world.getTileEntity(offPos);
					
					if(tile instanceof PistonTileEntity && ((PistonTileEntity) tile).getPistonState().getBlock() instanceof StoolBlock) {
						piston = (PistonTileEntity) tile;
						break;
					}
				}
				
				if(piston != null) {
					Direction dir = piston.getMotionDirection();
					move(MoverType.PISTON, new Vector3d((float) dir.getXOffset() * 0.33, (float) dir.getYOffset() * 0.33, (float) dir.getZOffset() * 0.33));
					
					didOffset = true;
				}
				
				dead = !didOffset;
			}
		}
		
		if(dead && !world.isRemote) {
			setDead();

			if(state.getBlock() instanceof StoolBlock)
				world.setBlockState(pos, state.with(StoolBlock.SAT_IN, false));
		}
	}

	@Override
	public double getMountedYOffset() {
		return -0.3;
	}
	
	@Override
	protected void registerData() {
		// NO-OP
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		// NO-OP
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		// NO-OP
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
