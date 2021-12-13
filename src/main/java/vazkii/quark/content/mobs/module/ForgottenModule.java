package vazkii.quark.content.mobs.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.ForgottenRenderer;
import vazkii.quark.content.mobs.entity.ForgottenEntity;
import vazkii.quark.content.mobs.item.ForgottenHatItem;

@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class ForgottenModule extends QuarkModule {

	public static EntityType<ForgottenEntity> forgottenType;

	public static Item forgotten_hat;

	@Config(description = "1 in this many Skeletons that spawn under the threshold are replaced with Forgotten.") 
	public double forgottenSpawnRate = 0.05;

	@Config public int maxHeightForSpawn = 20;

	@Override
	public void construct() {
		forgotten_hat = new ForgottenHatItem(this);

		forgottenType = EntityType.Builder.create(ForgottenEntity::new, EntityClassification.MONSTER)
				.size(0.7F, 2.4F)
				.trackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new ForgottenEntity(forgottenType, world))
				.build("forgotten");

		RegistryHelper.register(forgottenType, "forgotten");
		EntitySpawnHandler.addEgg(forgottenType, 0x969487, 0x3a3330, this, () -> true);
	}

	@Override
	public void setup() {
		super.setup();

		GlobalEntityTypeAttributes.put(forgottenType, ForgottenEntity.registerAttributes().create());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(forgottenType, ForgottenRenderer::new);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onSkeletonSpawn(LivingSpawnEvent.CheckSpawn event) {
		LivingEntity entity = event.getEntityLiving();
		Result result = event.getResult();
		
		if(entity.getType() == EntityType.SKELETON && entity instanceof MobEntity && result != Result.DENY && entity.getPosY() < maxHeightForSpawn && entity.world.rand.nextDouble() < forgottenSpawnRate) {
			MobEntity mob = (MobEntity) entity;

			if(result == Result.ALLOW || (mob.canSpawn(entity.world, event.getSpawnReason()) && mob.isNotColliding(entity.world))) {
				ForgottenEntity forgotten = new ForgottenEntity(forgottenType, entity.world);
				Vector3d epos = entity.getPositionVec();

				forgotten.setPositionAndRotation(epos.x, epos.y, epos.z, entity.rotationYaw, entity.rotationPitch);
				forgotten.prepareEquipment();
				entity.world.addEntity(forgotten);
				event.setResult(Result.DENY);
			}
		}
	}

}
