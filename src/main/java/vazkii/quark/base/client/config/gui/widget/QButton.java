package vazkii.quark.base.client.config.gui.widget;

import java.awt.Color;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.client.config.gui.QuarkConfigHomeScreen;
import vazkii.quark.base.client.handler.TopLayerTooltipHandler;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.MiscUtil;

public class QButton extends Button {
	
	private static final int ORANGE = 1;
	private static final int PURPLE = 2;
	private static final int RAINBOW = 3;
	private static final int QUARK = 4;
	
	private static final List<Celebration> CELEBRATIONS = new ArrayList<>();
	static {
		celebrate("quark", 21, Month.MARCH, QUARK);
		celebrate("vm", 29, Month.APRIL, PURPLE);
		celebrate("minecraft", 18, Month.NOVEMBER, ORANGE);
		
		celebrate("vns", 9, Month.APRIL, ORANGE);
		celebrate("vazkii", 22, Month.NOVEMBER, ORANGE);
		celebrate("wire", 23, Month.SEPTEMBER, ORANGE);
		
		celebrate("iad", 6, Month.APRIL, RAINBOW);
		celebrate("iad2", 26, Month.OCTOBER, RAINBOW);
		celebrate("idr", 8, Month.NOVEMBER, RAINBOW);
		celebrate("ld", 8, Month.OCTOBER, RAINBOW);
		celebrate("lvd", 26, Month.APRIL, RAINBOW);
		celebrate("ncod", 11, Month.OCTOBER, RAINBOW);
		celebrate("nbpd", 14, Month.JULY, RAINBOW);
		celebrate("ppad", 24, Month.MAY, RAINBOW);
		celebrate("tdr", 20, Month.NOVEMBER, RAINBOW);
		celebrate("tdv", 31, Month.MARCH, RAINBOW);
		celebrate("zdd", 1, Month.MARCH, RAINBOW);

		celebrate("afd", 1, Month.APRIL, QUARK);
		celebrate("wwd", 3, Month.MARCH, PURPLE);
		celebrate("hw", 31, Month.OCTOBER, ORANGE);
		celebrate("xmas", 25, Month.DECEMBER, PURPLE);
		celebrate("iwd", 8, Month.MARCH, PURPLE);
		celebrate("wpld", 5, Month.MAY, PURPLE);
		celebrate("iyd", 12, Month.AUGUST, PURPLE);
		celebrate("hrd", 9, Month.DECEMBER, PURPLE);
		celebrate("ny", 1, 3, Month.JANUARY, PURPLE);
		celebrate("doyouremember", 21, Month.SEPTEMBER, ORANGE);

		// Order is important, ensure mutli day ones are at the bottom
		celebrate("pm", 1, 30, Month.JUNE, RAINBOW);
		celebrate("baw", 16, 22, Month.SEPTEMBER, RAINBOW);
		celebrate("taw", 13, 19, Month.NOVEMBER, RAINBOW);
	}
	
	private static void celebrate(String name, int day, Month month, int tier) {
		celebrate(name, day, day, month, tier);
	}
	
	private static void celebrate(String name, int day, int end, Month month, int tier) {
		CELEBRATIONS.add(new Celebration(day, month.getValue(), (end - day), tier, name));
	}


	private final boolean gay;
	private Celebration celebrating;
	
	public QButton(int x, int y) {
		super(x, y, 20, 20, new StringTextComponent("q"), QButton::click);
		
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		
		gay = month == 6;
		
		for(Celebration c : CELEBRATIONS)
			if(c.running(day, month)) {
				celebrating = c;
				break;
			}
	}
	
	@Override
	public int getFGColor() {
		return gay ? Color.HSBtoRGB((ClientTicker.total / 200F), 1F, 1F) : 0x48DDBC;
	}
	
	@Override
	public void renderButton(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		super.renderButton(mstack, mouseX, mouseY, pticks);
		
		int iconIndex = Math.min(4, ContributorRewardHandler.localPatronTier);
		if(celebrating != null) {
			iconIndex = celebrating.tier;
		}
		
		if(iconIndex > 0) {
			RenderSystem.color3f(1F, 1F, 1F);
			int rx = x - 2;
			int ry = y - 2;
			
			int w = 9;
			int h = 9;
			
			int v = 26;
			
			if(celebrating != null) {
				rx -= 3;
				ry -= 2;
				w = 10;
				h = 10;
				v = 44;
				
				boolean hovered = mouseX >= x && mouseY >= y && mouseX < (x + width) && mouseY < (y + height);
				if(hovered)
					TopLayerTooltipHandler.setTooltip(Arrays.asList(I18n.format("quark.gui.celebration." + celebrating.name)), mouseX, mouseY);
			}
			
			int u = 256 - iconIndex * w;
			
			Minecraft.getInstance().textureManager.bindTexture(MiscUtil.GENERAL_ICONS);
			blit(mstack, rx, ry, u, v, w, h);
		}
	}
	
	public static void click(Button b) {
		Minecraft.getInstance().displayGuiScreen(new QuarkConfigHomeScreen(Minecraft.getInstance().currentScreen));
	}
	
	private static class Celebration {
		public final int day, month, len, tier;
		public final String name;
		
		public Celebration(int day, int month, int len, int tier, String name) {
			this.day = day;
			this.month = month;
			this.len = len;
			this.tier = tier;
			this.name = name;
		}
		
		// AFAIK none of the ones I'm tracking pass beyond a month so this 
		// lazy check is fine
		public boolean running(int day, int month) {
			return this.month == month && (this.day >= day && this.day <= (day + len));
		}
	}
	
}