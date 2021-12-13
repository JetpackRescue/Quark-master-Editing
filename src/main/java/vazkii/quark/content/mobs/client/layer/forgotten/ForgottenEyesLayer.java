package vazkii.quark.content.mobs.client.layer.forgotten;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;

@OnlyIn(Dist.CLIENT)
public class ForgottenEyesLayer<T extends SkeletonEntity, M extends SkeletonModel<T>> extends AbstractEyesLayer<T, M> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/forgotten/eye.png");
	private static final RenderType RENDER_TYPE = RenderType.getEyes(TEXTURE);

	public ForgottenEyesLayer(IEntityRenderer<T, M> rendererIn) {
		super(rendererIn);
	}

	@Override
	public RenderType getRenderType() {
		return RENDER_TYPE;
	}
}