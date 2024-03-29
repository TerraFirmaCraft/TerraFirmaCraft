/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.crafting.IShapedRecipe;

import net.dries007.tfc.util.Helpers;

public abstract class DamageInputsCraftingRecipe<R extends Recipe<CraftingContainer>> extends DelegateRecipe<R, CraftingContainer> implements CraftingRecipe
{
    protected static boolean isUnbreakable(ItemStack stack)
    {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean("Unbreakable");
    }

    protected DamageInputsCraftingRecipe(ResourceLocation id, R recipe)
    {
        super(id, recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); ++i)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.isDamageableItem())
            {
                items.set(i, Helpers.damageCraftingItem(stack, 1).copy());
            }
            else if (isUnbreakable(stack)) // unbreakable items are not damageable, but should still be able to be used in crafting
            {
                items.set(i, stack.copy());
            }
            else if (stack.hasCraftingRemainingItem())
            {
                items.set(i, stack.getCraftingRemainingItem());
            }
        }
        return items;
    }

    @Override
    public CraftingBookCategory category()
    {
        return CraftingBookCategory.MISC;
    }

    public static class Shapeless extends DamageInputsCraftingRecipe<Recipe<CraftingContainer>>
    {
        public Shapeless(ResourceLocation id, Recipe<CraftingContainer> recipe)
        {
            super(id, recipe);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.DAMAGE_INPUTS_SHAPELESS_CRAFTING.get();
        }
    }

    public static class Shaped extends DamageInputsCraftingRecipe<IShapedRecipe<CraftingContainer>> implements IRecipeDelegate.Shaped<CraftingContainer>
    {
        public Shaped(ResourceLocation id, IShapedRecipe<CraftingContainer> recipe)
        {
            super(id, recipe);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.DAMAGE_INPUT_SHAPED_CRAFTING.get();
        }
    }
}
