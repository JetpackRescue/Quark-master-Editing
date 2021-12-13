package vazkii.quark.content.mobs.item;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.mobs.entity.SoulBeadEntity;
import vazkii.quark.content.mobs.module.WraithModule;

public class SoulBeadItem extends QuarkItem {

	public SoulBeadItem(QuarkModule module) {
		super("soul_bead", module, new Item.Properties().group(ItemGroup.MISC));
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if(!worldIn.isRemote) {
			Structure<?> target = Structure.field_236378_n_; // Nether Fortress
            BlockPos blockpos = ((ServerWorld)worldIn).getChunkProvider().getChunkGenerator().func_235956_a_((ServerWorld)worldIn, target, playerIn.getPosition(), 100, false);

			if(blockpos != null) {
				itemstack.shrink(1);
				SoulBeadEntity entity = new SoulBeadEntity(WraithModule.soulBeadType, worldIn);
				entity.setTarget(blockpos.getX(), blockpos.getZ());
				
				Vector3d look = playerIn.getLookVec();
				entity.setPosition(playerIn.getPosX() + look.x * 2, playerIn.getPosY() + 0.25, playerIn.getPosZ() + look.z * 2);
				worldIn.addEntity(entity);

				worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), QuarkSounds.ITEM_SOUL_POWDER_SPAWN, SoundCategory.PLAYERS, 1F, 1F);
			}
		} else playerIn.swingArm(handIn);

		
        playerIn.addStat(Stats.ITEM_USED.get(this));
		return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
	}
	
}
