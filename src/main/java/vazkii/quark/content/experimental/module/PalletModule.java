package vazkii.quark.content.experimental.module;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.experimental.pallet.PalletBlock;
import vazkii.quark.content.experimental.pallet.PalletTileEntity;
import vazkii.quark.content.experimental.pallet.PalletTileEntityRenderer;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false)
public class PalletModule extends QuarkModule {

    public static TileEntityType<PalletTileEntity> tileEntityType;
	
    public static Block pallet;
    
	@Override
	public void construct() {
		pallet = new PalletBlock(this);
		
    	tileEntityType = TileEntityType.Builder.create(PalletTileEntity::new, pallet).build(null);
		RegistryHelper.register(tileEntityType, "pallet");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		ClientRegistry.bindTileEntityRenderer(tileEntityType, PalletTileEntityRenderer::new);
	}
	
}
