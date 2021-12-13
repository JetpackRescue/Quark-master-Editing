package vazkii.quark.content.building.recipe;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MixedChestRecipe implements ICraftingRecipe, IShapedRecipe<CraftingInventory> {
	
    public static final Serializer SERIALIZER = new Serializer();

	final ResourceLocation res;
	NonNullList<Ingredient> ingredients;
	
	final boolean log;
	final ItemStack output;
	final ITag.INamedTag<Item> tag;
	final ItemStack placeholder;
	
	public MixedChestRecipe(ResourceLocation res, boolean log) {
		this.res = res;
		
		this.log = log;
		this.output = new ItemStack(Items.CHEST, (log ? 4 : 1));
		this.tag = (log ? ItemTags.LOGS : ItemTags.PLANKS);
		this.placeholder = new ItemStack(log ? Items.OAK_LOG : Items.OAK_PLANKS);
	}
	
	@Override
	public boolean canFit(int x, int y) {
		return x == 3 && y == 3;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory arg0) {
		return output.copy();
	}

	@Override
	public ResourceLocation getId() {
		return res;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output.copy();	
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		if(inv.getStackInSlot(4).isEmpty()) {
			ItemStack first = null;
			boolean foundDifference = false;
			
			for(int i = 0; i < 9; i++)
				if(i != 4) { // ignore center
					ItemStack stack = inv.getStackInSlot(i);
					if(!stack.isEmpty() && stack.getItem().isIn(tag)) {
						if(first == null)
							first = stack;
						else if(!ItemStack.areItemsEqual(first, stack))
							foundDifference = true;
					} else return false;
				}
			
			return foundDifference;
		}
		
		return false;
	}

	@Override
	public int getRecipeWidth() {
		return 3;
	}

	@Override
	public int getRecipeHeight() {
		return 3;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		if(ingredients == null) {
			NonNullList<Ingredient> list = NonNullList.withSize(9, Ingredient.EMPTY);
			Ingredient ingr = Ingredient.fromStacks(placeholder);
			for(int i = 0; i < 8; i++)
				list.set(i < 4 ? i : i + 1, ingr);
			ingredients = list;
		}
		
		return ingredients;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MixedChestRecipe> {
		
        public Serializer() {
            setRegistryName("quark:mixed_chest");
        }

		@Override
		public MixedChestRecipe read(ResourceLocation arg0, JsonObject arg1) {
			return new MixedChestRecipe(arg0, arg1.get("log").getAsBoolean());
		}

		@Override
		public MixedChestRecipe read(ResourceLocation arg0, PacketBuffer arg1) {
			return new MixedChestRecipe(arg0, arg1.readBoolean());
		}

		@Override
		public void write(PacketBuffer arg0, MixedChestRecipe arg1) {
			arg0.writeBoolean(arg1.log);
		}
		
	}

}
