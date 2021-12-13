package vazkii.quark.base.client.config.gui;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.module.config.type.ColorMatrixConfig;

public class ColorMatrixInputScreen extends AbstractInputScreen<ColorMatrixConfig> {

	final ColorMatrixConfig color;
	final ColorMatrixConfig mutable;
	final ConfigCategory category;
	
	public ColorMatrixInputScreen(Screen parent, ColorMatrixConfig colorMatrix, ConfigCategory category) {
		super(parent);
		
		this.color = colorMatrix;
		this.mutable = colorMatrix.copy();
		this.category = category;
	}

	@Override
	void onInit() {
		int w = 70;
		int p = 12;
		int x = width / 2 - 33;
		int y = 55;
		
		ITextComponent prefix = new StringTextComponent("");
		ITextComponent suffix = new StringTextComponent("");
		
		for(int i = 0; i < 9; i++)
			addButton(new Slider(x + w * (i % 3), y + 25 * (i / 3), w - p, 20, prefix, suffix, 0f, 2f, color.colorMatrix[i], false, false, this::onSlide));
		
		addButton(new Button(x + w * 0, y + 115, w - p, 20, new StringTextComponent("Identity"), this::onSlide));
		addButton(new Button(x + w * 1, y + 115, w - p, 20, new StringTextComponent("Dreary"), this::onSlide));
		addButton(new Button(x + w * 2, y + 115, w - p, 20, new StringTextComponent("Vibrant"), this::onSlide));
	}
	
	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		super.render(mstack, mouseX, mouseY, pticks);
		
		int x = width / 2 - 203;
		int y = 10;
		int size = 60;

		int titleLeft = width / 2 + 66;
		drawCenteredString(mstack, font, new StringTextComponent(category.getGuiDisplayName()).mergeStyle(TextFormatting.BOLD), titleLeft, 20, 0xFFFFFF);
		drawCenteredString(mstack, font, new StringTextComponent("Presets"), titleLeft, 155, 0xFFFFFF);

		int sliders = 0;
		boolean needsUpdate = false;
		for(Widget w : buttons)
			if(w instanceof Slider) {
				Slider s = (Slider) w;
				if(mouseX < s.x || mouseY < s.y || mouseX >= s.x + s.getWidth() || mouseY >= s.y + s.getHeightRealms())
					s.dragging = false;
				
				double val = correct(s);
				double curr = mutable.colorMatrix[sliders];
				if(curr != val) {
					mutable.colorMatrix[sliders] = val;
					needsUpdate = true;
				}
				
				String displayVal = String.format("%.2f", val);
				font.drawStringWithShadow(mstack, displayVal, s.x + s.getWidth() / 2 - font.getStringWidth(displayVal) / 2 , s.y + 6, 0xFFFFFF);
				
				switch(sliders) {
				case 0:
					font.drawStringWithShadow(mstack, "R =", s.x - 20, s.y + 5, 0xFF0000);
					font.drawStringWithShadow(mstack, "R", s.x + s.getWidth() / 2 - 2, s.y - 12, 0xFF0000);
					break;
				case 1:
					font.drawStringWithShadow(mstack, "G", s.x + s.getWidth() / 2 - 2, s.y - 12, 0x00FF00);
					break;
				case 2:
					font.drawStringWithShadow(mstack, "B", s.x + s.getWidth() / 2 - 2, s.y - 12, 0x0077FF);
					break;
				case 3:
					font.drawStringWithShadow(mstack, "G =", s.x - 20, s.y + 5, 0x00FF00);
					break;
				case 6:
					font.drawStringWithShadow(mstack, "B =", s.x - 20, s.y + 5, 0x0077FF);
					break;
				default: break;
				}
				if((sliders % 3) != 0)
					font.drawStringWithShadow(mstack, "+", s.x - 9, s.y + 5, 0xFFFFFF);
				
				sliders++;
			}
		
		String[] biomes = { "plains", "forest", "mountains", "jungle", "savanna", "swamp" };
		int[] colors = { 0xff91bd59, 0xff79c05a, 0xff8ab689, 0xff59c93c, 0xffbfb755, 0xff6a7039 };
		int[] folliageColors = { 0xff77ab2f, 0xff59ae30, 0xff6da36b, 0xff30bb0b, 0xffaea42a, 0xff6a7039 };
		for(int i = 0; i < biomes.length; i++) {
			String name = biomes[i];
			int color = colors[i];
			int folliage = folliageColors[i];
			
			int convolved = mutable.convolve(color);
			int convolvedFolliage = mutable.convolve(folliage);
			
			int cx = x + (i % 2) * (size + 5);
			int cy = y + (i / 2) * (size + 5);
			
			fill(mstack, cx - 1, cy - 1, cx + size + 1, cy + size + 1, 0xFF000000);
			fill(mstack, cx, cy, cx + size, cy + size, convolved);
			fill(mstack, cx + size / 2 - 1, cy + size / 2 - 1, cx + size, cy + size, 0x22000000);
			fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage);
			
			font.drawString(mstack, name, cx + 2, cy + 2, 0x55000000);
			
			minecraft.getItemRenderer().renderItemIntoGUI(new ItemStack(Items.OAK_SAPLING), cx + size - 18, cy + size  - 16);
			mstack.push();
			mstack.translate(0, 0, 999);
			fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage & 0x55FFFFFF);
			mstack.pop();
		}
		
		if(needsUpdate)
			update();
	}
	
	private double correct(Slider s) {
		double val = s.getValue();
		val = correct(val, 1.0, s);
		val = correct(val, 0.5, s);
		val = correct(val, 1.5, s);
		return val;
	}
	
	private double correct(double val, double correct, Slider s) {
		if(Math.abs(val - correct) < 0.02) {
			s.setValue(correct);
			return correct;
		}
		return val;
	}

	@Override
	ColorMatrixConfig compute() {
		return mutable;
	}
	
	void onSlide(Button button) {
		String name = button.getMessage().getString();
		double[][] matrices = {
				{
					1, 0, 0,
					0, 1, 0,
					0, 0, 1
				},
				{
					1.24, 0, 0,
					0, 0.84, 0,
					0, 0.16, 0.36
				},
				{
					1, 0, 0,
					0.24, 1, 0.24,
					0, 0, 0.6
				}
		};
		
		int idx = 0;
		switch(name) {
		case "Dreary": idx = 1; break;
		case "Vibrant": idx = 2; break;
		}
		
		int sliders = 0;
		mutable.colorMatrix = Arrays.copyOf(matrices[idx], matrices[idx].length);
		
		for(Widget w : buttons)
			if(w instanceof Slider) {
				Slider s = (Slider) w;
				s.setValue(matrices[idx][sliders]);
				sliders++;
			}
		update();
	}
	
	@Override
	void setDefault() {
		mutable.colorMatrix = Arrays.copyOf(color.defaultMatrix, color.defaultMatrix.length);
	}

	@Override
	void reset() {
		mutable.colorMatrix = Arrays.copyOf(mutable.defaultMatrix, mutable.defaultMatrix.length);
	}

	@Override
	boolean isErrored() {
		return false;
	}

	@Override
	boolean isDirty() {
		return !color.equals(mutable);
	}
	
	@Override
	void commit() {
		color.inherit(mutable);
	}

}
