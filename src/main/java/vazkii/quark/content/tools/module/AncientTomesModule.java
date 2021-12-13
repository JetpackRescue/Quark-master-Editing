package vazkii.quark.content.tools.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.item.AncientTomeItem;
import vazkii.quark.content.tools.loot.EnchantTome;
import vazkii.quark.content.world.module.MonsterBoxModule;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class AncientTomesModule extends QuarkModule {

	@Config(description = "Set to 0 to not generate in Dungeons") 
	public static int dungeonWeight = 20;

	@Config(description = "Set to 0 to not generate in Stronghold Libraries")
	public static int libraryWeight = 30;

	@Config(description = "Set to 0 to not generate in Bastions")
	public static int bastionWeight = 25;

	@Config(description = "Set to 0 to not generate in Woodland Mansions")
	public static int woodlandMansionWeight = 15;

	@Config(description = "Set to 0 to not generate in Nether Fortresses")
	public static int netherFortressWeight = 0;

	@Config(description = "Set to 0 to not generate in Underwater Ruins")
	public static int underwaterRuinWeight = 0;

	@Config(description = "Set to 0 to not generate in Monster Boxes")
	public static int monsterBoxWeight = 5;

	@Config public static int itemQuality = 2;

	@Config public static int normalUpgradeCost = 10;
	@Config public static int limitBreakUpgradeCost = 30;

	public static LootFunctionType tomeEnchantType;

	@Config(name = "Valid Enchantments")
	public static List<String> enchantNames = generateDefaultEnchantmentList();

	public static Item ancient_tome;
	public static final List<Enchantment> validEnchants = new ArrayList<>();
	private static boolean initialized = false;

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		int weight = 0;
		ResourceLocation res = event.getName();
		if(res.equals(LootTables.CHESTS_STRONGHOLD_LIBRARY))
			weight = libraryWeight;
		else if(res.equals(LootTables.CHESTS_SIMPLE_DUNGEON))
			weight = dungeonWeight;
		else if(res.equals(LootTables.CHESTS_NETHER_BRIDGE))
			weight = netherFortressWeight;
		else if(res.equals(LootTables.CHESTS_WOODLAND_MANSION))
			weight = woodlandMansionWeight;
		else if(res.equals(LootTables.CHESTS_UNDERWATER_RUIN_BIG) || res.equals(LootTables.CHESTS_UNDERWATER_RUIN_SMALL))
			weight = underwaterRuinWeight;
		else if(res.equals(LootTables.BASTION_TREASURE))
			weight = bastionWeight;

		else if(res.equals(MonsterBoxModule.MONSTER_BOX_LOOT_TABLE))
			weight = monsterBoxWeight;

		if(weight > 0) {
			LootEntry entry = ItemLootEntry.builder(ancient_tome)
					.weight(weight)
					.quality(itemQuality)
					.acceptFunction(() -> new EnchantTome(new ILootCondition[0]))
					.build();

			MiscUtil.addToLootTable(event.getTable(), entry);
		}
	}

	@Override
	public void construct() {
		ancient_tome = new AncientTomeItem(this);

		tomeEnchantType = new LootFunctionType(new EnchantTome.Serializer());
		Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Quark.MOD_ID, "tome_enchant"), tomeEnchantType);

	}

	@Override
	public void setup() {
		setupEnchantList();
		initialized = true;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if(!left.isEmpty() && !right.isEmpty() ) {
			if(right.getItem() == ancient_tome) {
				Enchantment ench = getTomeEnchantment(right);
				Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(left);

				if(ench != null && enchants.containsKey(ench) && enchants.get(ench) <= ench.getMaxLevel()) {
					int lvl = enchants.get(ench) + 1;
					enchants.put(ench, lvl);

					ItemStack copy = left.copy();
					EnchantmentHelper.setEnchantments(enchants, copy);

					event.setOutput(copy);
					event.setCost(lvl > ench.getMaxLevel() ? limitBreakUpgradeCost : normalUpgradeCost);
				}
			} 
			
			else if(right.getItem() == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(right);
				Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(left);
				boolean hasOverLevel = false;
				boolean hasMatching = false;
				for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
					Enchantment enchantment = entry.getKey();
					if(enchantment == null)
						continue;

					int level = entry.getValue();
					if (level > enchantment.getMaxLevel()) {
						hasOverLevel = true;
						if (enchantment.canApply(left)) {
							hasMatching = true;
							//remove incompatible enchantments
							for (Iterator<Enchantment> iterator = currentEnchants.keySet().iterator(); iterator.hasNext(); ) {
								Enchantment comparingEnchantment = iterator.next();
								if (comparingEnchantment == enchantment)
									continue;

								if (!comparingEnchantment.isCompatibleWith(enchantment)) {
									iterator.remove();
								}
							}
							currentEnchants.put(enchantment, level);
						}
					} else if (enchantment.canApply(left)) {
						boolean compatible = true;
						//don't apply incompatible enchantments
						for (Enchantment comparingEnchantment : currentEnchants.keySet()) {
							if (comparingEnchantment == enchantment)
								continue;

							if (comparingEnchantment != null && !comparingEnchantment.isCompatibleWith(enchantment)) {
								compatible = false;
								break;
							}
						}
						if (compatible) {
							currentEnchants.put(enchantment, level);
						}
					}
				}

				if (hasOverLevel) {
					if (hasMatching) {
						ItemStack out = left.copy();
						EnchantmentHelper.setEnchantments(currentEnchants, out);
						String name = event.getName();
						int cost = normalUpgradeCost;
						
						if(name != null && !name.isEmpty() && (!out.hasDisplayName() || !out.getDisplayName().getString().equals(name))) {
							out.setDisplayName(new StringTextComponent(name));
							cost++;
						}
						
						event.setOutput(out);
						event.setCost(cost);
					} else {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	private static List<String> generateDefaultEnchantmentList() {
		Enchantment[] enchants = new Enchantment[] {
				Enchantments.FEATHER_FALLING,
				Enchantments.THORNS,
				Enchantments.SHARPNESS,
				Enchantments.SMITE,
				Enchantments.BANE_OF_ARTHROPODS,
				Enchantments.KNOCKBACK,
				Enchantments.FIRE_ASPECT,
				Enchantments.LOOTING,
				Enchantments.SWEEPING,
				Enchantments.EFFICIENCY,
				Enchantments.UNBREAKING,
				Enchantments.FORTUNE,
				Enchantments.POWER,
				Enchantments.PUNCH,
				Enchantments.LUCK_OF_THE_SEA,
				Enchantments.LURE,
				Enchantments.LOYALTY,
				Enchantments.RIPTIDE,
				Enchantments.IMPALING,
				Enchantments.PIERCING
		};

		List<String> strings = new ArrayList<>();
		for(Enchantment e : enchants)
			if(e != null && e.getRegistryName() != null)
				strings.add(e.getRegistryName().toString());

		return strings;
	}

	@Override
	public void configChanged() {
		if(initialized)
			setupEnchantList();
	}

	private void setupEnchantList() {
		MiscUtil.initializeEnchantmentList(enchantNames, validEnchants);
		validEnchants.removeIf((ench) -> ench.getMaxLevel() == 1);
	}

	public static Enchantment getTomeEnchantment(ItemStack stack) {
		if (stack.getItem() != ancient_tome)
			return null;

		ListNBT listnbt = EnchantedBookItem.getEnchantments(stack);

		for(int i = 0; i < listnbt.size(); ++i) {
			CompoundNBT compoundnbt = listnbt.getCompound(i);
			Optional<Enchantment> opt = Registry.ENCHANTMENT.getOptional(ResourceLocation.tryCreate(compoundnbt.getString("id")));
			if(opt.isPresent())
				return opt.orElse(null);
		}

		return null;
	}

}
