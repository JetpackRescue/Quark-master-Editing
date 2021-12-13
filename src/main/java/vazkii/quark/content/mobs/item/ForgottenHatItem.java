package vazkii.quark.content.mobs.item;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.mobs.client.model.ForgottenHatModel;

public class ForgottenHatItem extends ArmorItem implements IQuarkItem {

	private static final String TEXTURE = Quark.MOD_ID + ":textures/misc/forgotten_hat_worn.png";

	private final QuarkModule module;
	private Multimap<Attribute, AttributeModifier> attributes;

	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("rawtypes")
	private BipedModel model;

	public ForgottenHatItem(QuarkModule module) {
		super(ArmorMaterial.LEATHER, EquipmentSlotType.HEAD, 
				new Item.Properties()
				.maxStackSize(1)
				.maxDamage(0)
				.group(ItemGroup.TOOLS)
				.rarity(Rarity.RARE));

		RegistryHelper.registerItem(this, "forgotten_hat");
		this.module = module;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return TEXTURE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("unchecked")
	public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
		if(model == null)
			model = new ForgottenHatModel();

		return (A) model;
	}

	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack) {
		return false;
	}


	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if(attributes == null) {
			Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			UUID uuid = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
			builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", 1, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.LUCK, new AttributeModifier(uuid, "Armor luck modifier", 1, AttributeModifier.Operation.ADDITION));
			builder.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(uuid, "Armor reach modifier", 2, AttributeModifier.Operation.ADDITION));

			attributes = builder.build();
		}


		return slot == this.slot ? attributes : super.getAttributeModifiers(slot);
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public boolean isEnabled() {
		return module != null && module.enabled;
	}


}
