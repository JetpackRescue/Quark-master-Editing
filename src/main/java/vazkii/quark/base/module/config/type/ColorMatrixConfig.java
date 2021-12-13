package vazkii.quark.base.module.config.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.config.gui.CategoryScreen;
import vazkii.quark.base.client.config.gui.ColorMatrixInputScreen;
import vazkii.quark.base.client.config.gui.WidgetWrapper;
import vazkii.quark.base.client.config.gui.widget.IWidgetProvider;
import vazkii.quark.base.client.config.gui.widget.PencilButton;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class ColorMatrixConfig extends AbstractConfigType implements IWidgetProvider {

	@Config List<Double> r; 
	@Config List<Double> g;
	@Config List<Double> b;

	public final double[] defaultMatrix;
	public double[] colorMatrix;

	public ColorMatrixConfig(double[] defaultMatrix) {
		assert defaultMatrix.length == 9;

		this.defaultMatrix = defaultMatrix;
		this.colorMatrix = Arrays.copyOf(defaultMatrix, defaultMatrix.length);

		r = Arrays.asList(defaultMatrix[0], defaultMatrix[1], defaultMatrix[2]);
		g = Arrays.asList(defaultMatrix[3], defaultMatrix[4], defaultMatrix[5]);
		b = Arrays.asList(defaultMatrix[6], defaultMatrix[7], defaultMatrix[8]);
	}

	@Override
	public void onReload(ConfigFlagManager flagManager) {
		try {
			colorMatrix = new double[] {
					r.get(0), r.get(1), r.get(2),
					g.get(0), g.get(1), g.get(2),
					b.get(0), b.get(1), b.get(2)
			};
		} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			colorMatrix = Arrays.copyOf(defaultMatrix, defaultMatrix.length);
		}
	}

	public void inherit(ColorMatrixConfig other) {
		r = other.r;
		g = other.g;
		b = other.b;
		colorMatrix = Arrays.copyOf(other.colorMatrix, other.colorMatrix.length);

		if(category != null) {
			category.refresh();
			category.updateDirty();
		}
	}

	public ColorMatrixConfig copy() {
		ColorMatrixConfig newMatrix = new ColorMatrixConfig(colorMatrix);
		newMatrix.inherit(this);
		return newMatrix;
	}

	public int convolve(int color) {
		int r = color >> 16 & 0xFF;
		int g = color >> 8 & 0xFF;
		int b = color & 0xFF;

		int outR = clamp((int) ((double) r * colorMatrix[0] + (double) g * colorMatrix[1] + (double) b * colorMatrix[2]));
		int outG = clamp((int) ((double) r * colorMatrix[3] + (double) g * colorMatrix[4] + (double) b * colorMatrix[5]));
		int outB = clamp((int) ((double) r * colorMatrix[6] + (double) g * colorMatrix[7] + (double) b * colorMatrix[8]));

		return 0xFF000000 | (((outR & 0xFF) << 16) + ((outG & 0xFF) << 8) + (outB & 0xFF));
	}


	private int clamp(int val) {
		return Math.min(0xFF, Math.max(0, val));
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof ColorMatrixConfig && Arrays.equals(((ColorMatrixConfig) obj).colorMatrix, colorMatrix));
	}
	
	@Override
	public int hashCode() {
		return colorMatrix.hashCode();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addWidgets(CategoryScreen parent, List<WidgetWrapper> widgets) {
		Minecraft minecraft = Minecraft.getInstance();
		widgets.add(new WidgetWrapper(new PencilButton(230, 3, b -> minecraft.displayGuiScreen(new ColorMatrixInputScreen(parent, this, category)))));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getSubtitle() {
		return "[" + Arrays.stream(colorMatrix).boxed().map(d -> String.format("%.1f", d)).collect(Collectors.joining(", ")) + "]";
	}

}
