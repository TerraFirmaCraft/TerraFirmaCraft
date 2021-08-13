/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.IShapedRecipe;

import net.dries007.tfc.util.Helpers;

public abstract class DamageInputsCraftingRecipe<R extends IRecipe<CraftingInventory>> extends DelegateRecipe<R, CraftingInventory> implements ICraftingRecipe
{
    protected DamageInputsCraftingRecipe(ResourceLocation id, R recipe)
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

    public static class Shapeless extends DamageInputsCraftingRecipe<IRecipe<CraftingInventory>>
    {
        public Shapeless(ResourceLocation id, IRecipe<CraftingInventory> recipe)
        {
            super(id, recipe);
        }

        @Override
        public IRecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.DAMAGE_INPUTS_SHAPELESS_CRAFTING.get();
        }
    }

    public static class Shaped extends DamageInputsCraftingRecipe<IShapedRecipe<CraftingInventory>> implements IRecipeDelegate.Shaped<CraftingInventory>
    {
        public Shaped(ResourceLocation id, IShapedRecipe<CraftingInventory> recipe)
        {
            super(id, recipe);
        }

        @Override
        public IRecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.DAMAGE_INPUT_SHAPED_CRAFTING.get();
        }
    }
}
