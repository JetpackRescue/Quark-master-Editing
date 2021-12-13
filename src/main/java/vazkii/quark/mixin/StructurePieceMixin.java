package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import vazkii.quark.base.handler.StructureBlockReplacementHandler;

@Mixin(StructurePiece.class)
public class StructurePieceMixin {

	@ModifyArg(
		    method = "generateChest(Lnet/minecraft/world/IServerWorld;Lnet/minecraft/util/math/MutableBoundingBox;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/block/BlockState;)Z",
		    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"),
		    index = 1
		)
	protected BlockState modifyBlockstateForChest(BlockState state) {
		return StructureBlockReplacementHandler.getResultingBlockState(state);
	}
	
	@ModifyArg(
		    method = "setBlockState(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/MutableBoundingBox;)V",
		    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ISeedReader;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"),
		    index = 1
		)
	protected BlockState modifyBlockstate(BlockState state) {
		return StructureBlockReplacementHandler.getResultingBlockState(state);
	}
	
}
