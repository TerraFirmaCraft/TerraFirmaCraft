package net.dries007.tfc.common.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.Helpers;

public class DamageInputsCraftingRecipe extends DelegatingRecipe<CraftingInventory> implements ICraftingRecipe
{
    protected DamageInputsCraftingRecipe(ResourceLocation id, IRecipe<CraftingInventory> recipe)
    {
        super(id, recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); ++i)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.isDamageableItem())
            {
                Helpers.damageCraftingItem(stack, 1);
            }
            else if (stack.hasContainerItem())
            {
                items.set(i, stack.getContainerItem());
            }
        }
        return items;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.DAMAGE_INPUTS_CRAFTING.get();
    }
}
