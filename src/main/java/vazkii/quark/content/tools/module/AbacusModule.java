package vazkii.quark.content.tools.module;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.item.AbacusItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class AbacusModule extends QuarkModule {

	public static Item abacus;

	@Override
	public void construct() {
		abacus = new AbacusItem(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		enqueue(() -> ItemModelsProperties.registerProperty(abacus, new ResourceLocation("count"), AbacusItem::count));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onHUDRender(RenderGameOverlayEvent event) {
		if(event.getType() == ElementType.ALL) {
			Minecraft mc = Minecraft.getInstance();
			PlayerEntity player = mc.player;
			if(player != null) {
				ItemStack stack = player.getHeldItemMainhand();
				if(!(stack.getItem() instanceof AbacusItem))
					stack = player.getHeldItemOffhand();

				if(stack.getItem() instanceof AbacusItem) {
					int distance = AbacusItem.getCount(stack, player);
					if(distance > -1) {
						MainWindow window = event.getWindow();
						int x = window.getScaledWidth() / 2 + 10;
						int y = window.getScaledHeight() / 2 - 7;

						mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
						
						String distStr = distance < AbacusItem.MAX_COUNT ? Integer.toString(distance) : (AbacusItem.MAX_COUNT + "+");
						mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(), distStr, x + 17, y + 5, 0xFFFFFF);
					}
				}
			}
		}
	}
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onHighlightBlock(DrawHighlightEvent.HighlightBlock event) {
		IVertexBuilder bufferIn = event.getBuffers().getBuffer(RenderType.getLines());

		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		if(player != null) {
			ItemStack stack = player.getHeldItemMainhand();
			if(!(stack.getItem() instanceof AbacusItem))
				stack = player.getHeldItemOffhand();

			if(stack.getItem() instanceof AbacusItem) {
				int distance = AbacusItem.getCount(stack, player);
				if(distance > -1 && distance <= AbacusItem.MAX_COUNT) {
					BlockPos target = AbacusItem.getBlockPos(stack);

					ActiveRenderInfo info = event.getInfo();
					Vector3d view = info.getProjectedView();

					VoxelShape shape = VoxelShapes.create(new AxisAlignedBB(target));

					RayTraceResult result = mc.objectMouseOver;
					if(result instanceof BlockRayTraceResult) {
						BlockPos source = ((BlockRayTraceResult) result).getPos();
						
						int diffX = source.getX() - target.getX();
						int diffY = source.getY() - target.getY();
						int diffZ = source.getZ() - target.getZ();
						
						if(diffX != 0)
							shape = VoxelShapes.or(shape, VoxelShapes.create(new AxisAlignedBB(target).expand(diffX, 0, 0)));
						if(diffY != 0)
							shape = VoxelShapes.or(shape, VoxelShapes.create(new AxisAlignedBB(target.add(diffX, 0, 0)).expand(0, diffY, 0)));
						if(diffZ != 0)
							shape = VoxelShapes.or(shape, VoxelShapes.create(new AxisAlignedBB(target.add(diffX, diffY, 0)).expand(0, 0, diffZ)));
					}

					if(shape != null) {
						List<AxisAlignedBB> list = shape.toBoundingBoxList();
						MatrixStack matrixStackIn = event.getMatrix();
						
						// everything from here is a vanilla copy pasta but tweaked to have the same colors
						
						double xIn = -view.x;
						double yIn = -view.y;
						double zIn = -view.z;
						
						for(int j = 0; j < list.size(); ++j) {
							float r = 0F;
							float g = 0F;
							float b = 0F;
							float a = 0.4F;
							
							AxisAlignedBB axisalignedbb = list.get(j);

							VoxelShape individual = VoxelShapes.create(axisalignedbb.offset(0.0D, 0.0D, 0.0D));
							Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
							individual.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
								bufferIn.pos(matrix4f, (float)(minX + xIn), (float)(minY + yIn), (float)(minZ + zIn)).color(r, g, b, a).endVertex();
								bufferIn.pos(matrix4f, (float)(maxX + xIn), (float)(maxY + yIn), (float)(maxZ + zIn)).color(r, g, b, a).endVertex();
							});
						}
						
						event.setCanceled(true);
					}
				}
			}
		}
	}

}
