package vazkii.quark.content.client.module;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.ColorMatrixConfig;

import java.util.List;
import java.util.Map;

@LoadModule(category = ModuleCategory.CLIENT)
public class GreenerGrassModule extends QuarkModule {

	@Config public static boolean affectLeaves = true;

	@Config public static List<String> blockList = Lists.newArrayList(
			"minecraft:large_fern", 
			"minecraft:tall_grass",
			"minecraft:grass_block",
			"minecraft:fern",
			"minecraft:grass",
			"minecraft:potted_fern",
			"minecraft:sugar_cane",
			"environmental:giant_tall_grass",
			"valhelsia_structures:grass_block");

	@Config public static List<String> leavesList = Lists.newArrayList(
			"minecraft:spruce_leaves", 
			"minecraft:birch_leaves",
			"minecraft:oak_leaves",
			"minecraft:jungle_leaves",
			"minecraft:acacia_leaves",
			"minecraft:dark_oak_leaves",
			"atmospheric:rosewood_leaves",
			"atmospheric:morado_leaves",
			"atmospheric:yucca_leaves",
			"autumnity:maple_leaves",
			"environmental:willow_leaves",
			"environmental:hanging_willow_leaves",
			"minecraft:vine");
	
	@Config public static ColorMatrixConfig colorMatrix = new ColorMatrixConfig(new double[] {
			0.89, 0.00, 0.00,
			0.00, 1.11, 0.00,
			0.00, 0.00, 0.89
	});

	@Override
	public void firstClientTick() {
		registerGreenerColor(blockList, false);
		registerGreenerColor(leavesList, true);
	}

	@OnlyIn(Dist.CLIENT)
	private void registerGreenerColor(Iterable<String> ids, boolean leaves) {
		BlockColors colors = Minecraft.getInstance().getBlockColors();

		// Can't be AT'd as it's changed by forge
		Map<IRegistryDelegate<Block>, IBlockColor> map = ObfuscationReflectionHelper.getPrivateValue(BlockColors.class, colors, "field_186725_a");

		for(String id : ids) {
			Registry.BLOCK.getOptional(new ResourceLocation(id)).ifPresent(b -> {
				if (b.delegate == null)
					return;
				IBlockColor color = map.get(b.delegate);
				if(color != null)
					colors.register(getGreenerColor(color, leaves), b);
			});
		}
	}

	@OnlyIn(Dist.CLIENT)
	private IBlockColor getGreenerColor(IBlockColor color, boolean leaves) {
		return (state, world, pos, tintIndex) -> {
			int originalColor = color.getColor(state, world, pos, tintIndex);
			if(!enabled || (leaves && !affectLeaves))
				return originalColor;
			
			return colorMatrix.convolve(originalColor);
		};
	}

}
