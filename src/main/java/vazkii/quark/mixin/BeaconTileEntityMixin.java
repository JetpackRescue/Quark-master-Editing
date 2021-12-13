package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.tileentity.BeaconTileEntity;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule;

@Mixin(BeaconTileEntity.class)
public class BeaconTileEntityMixin {

	// This captures the for loop inside tick that computes the beacon segments
	@ModifyConstant(method = "tick", constant = @Constant(intValue = 0))
	public int tick(int val) {
		return CaveCrystalUndergroundBiomeModule.tickBeacon((BeaconTileEntity) (Object) this);
	}
	
}
