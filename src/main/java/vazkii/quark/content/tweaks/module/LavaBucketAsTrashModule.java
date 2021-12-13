package vazkii.quark.content.tweaks.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tweaks.capability.LavaBucketDropIn;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class LavaBucketAsTrashModule extends QuarkModule {

    private static final ResourceLocation LAVA_BUCKET_CAP = new ResourceLocation(Quark.MOD_ID, "lava_bucket_drop_in");

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() == Items.LAVA_BUCKET)
            event.addCapability(LAVA_BUCKET_CAP, new LavaBucketDropIn());
    }
	
}
