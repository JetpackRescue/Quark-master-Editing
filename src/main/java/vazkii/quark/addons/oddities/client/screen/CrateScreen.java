package vazkii.quark.addons.oddities.client.screen;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import vazkii.quark.addons.oddities.container.CrateContainer;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.InventoryButtonHandler;
import vazkii.quark.base.client.handler.InventoryButtonHandler.ButtonTargetType;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.content.client.module.ChestSearchingModule;

public class CrateScreen extends ContainerScreen<CrateContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/gui/crate.png");

	final int inventoryRows;
	List<Rectangle2d> extraAreas;

	public CrateScreen(CrateContainer container, PlayerInventory inv, ITextComponent component) {
		super(container, inv, component);
		
		inventoryRows = CrateContainer.numRows;
		ySize = 114 + this.inventoryRows * 18;
		playerInventoryTitleY = ySize - 94;
	}
	
	@Override
	protected void init() {
		super.init();
		
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		extraAreas = Lists.newArrayList(new Rectangle2d(i + xSize, j, 23, 136));
	} 
	// TODO scroll with mouse 
	
	public List<Rectangle2d> getExtraAreas() {
		return extraAreas;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		container.scroll(delta < 0, true);
		return true;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		minecraft.getTextureManager().bindTexture(TEXTURE);
		
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(matrixStack, i, j, 0, 0, xSize + 20, ySize);
		
		int maxScroll = (container.getStackCount() / CrateContainer.numCols) * CrateContainer.numCols;
		int currScroll = (container.scroll * 95) / Math.max(1, maxScroll);
		
		int u = 232 + (maxScroll == 0 ? 12 : 0);
		int by = j + 18 + currScroll;
		blit(matrixStack, i + xSize, by, u, 0, 12, 15);

		if(!ChestSearchingModule.searchEnabled) {
			String s = container.getTotal() + "/" + CrateModule.maxItems;
			
			int color = MiscUtil.getGuiTextColor("crate_count");
			font.drawString(matrixStack, s, i + this.xSize - font.getStringWidth(s) - 8 - InventoryButtonHandler.getActiveButtons(ButtonTargetType.CONTAINER_INVENTORY).size() * 12, j + 6, color);
		}
	}

}
