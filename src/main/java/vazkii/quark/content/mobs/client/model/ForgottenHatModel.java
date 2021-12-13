package vazkii.quark.content.mobs.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.render.BaseArmorModel;

@OnlyIn(Dist.CLIENT)
public class ForgottenHatModel extends BaseArmorModel {

	private final ModelRenderer piece1;
	private final ModelRenderer piece2;
	
	private final ModelRenderer base;
	
	public ForgottenHatModel() {
		super(EquipmentSlotType.HEAD);
		base = new ModelRenderer(this);
		
		textureHeight = 64;
		textureWidth = 64;
		
		piece1 = new ModelRenderer(this, 0, 0);
		piece1.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, 0.6F);
		
		piece2 = new ModelRenderer(this, 0, 18);
		piece2.addBox(-6.0F, -6.0F, -6.0F, 12, 1, 12, 0.0F);
		
		base.addChild(piece1);
		base.addChild(piece2);

		bipedHead = base;
		bipedHeadwear = base;
	}

}
