package vazkii.quark.addons.oddities.module;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.block.CrateBlock;
import vazkii.quark.addons.oddities.client.render.PipeTileEntityRenderer;
import vazkii.quark.addons.oddities.client.screen.CrateScreen;
import vazkii.quark.addons.oddities.container.CrateContainer;
import vazkii.quark.addons.oddities.tile.CrateTileEntity;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.RequiredModTooltipHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.ODDITIES)
public class CrateModule extends QuarkModule {

    public static TileEntityType<CrateTileEntity> crateType;
	public static ContainerType<CrateContainer> containerType;
	
	public static Block crate;
	
	@Config public static int maxItems = 640;

	@Override
	public void construct() {
		crate = new CrateBlock(this);
		
		containerType = IForgeContainerType.create(CrateContainer::fromNetwork);
		RegistryHelper.register(containerType, "crate");
		
		crateType = TileEntityType.Builder.create(CrateTileEntity::new, crate).build(null);
		RegistryHelper.register(crateType, "crate");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		ScreenManager.registerFactory(containerType, CrateScreen::new);
	}
	
}
