/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.common.capabilities.food.IFood;

public class HasTraitIngredient extends TraitIngredient
{
    public static HasTraitIngredient of(@Nullable Ingredient delegate, FoodTrait trait)
    {
        return new HasTraitIngredient(delegate, trait);
    }

    public static HasTraitIngredient of(FoodTrait trait)
    {
        return new HasTraitIngredient(null, trait);
    }

    public HasTraitIngredient(@Nullable Ingredient delegate, FoodTrait trait)
    {
        super(delegate, trait);
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return super.test(stack) && stack != null && FoodCapability.hasTrait(stack, trait);
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return TraitSerializer.HAS_TRAIT;
    }

    @Nullable
    @Override
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        final @Nullable IFood food = FoodCapability.get(stack);
        if (food != null)
        {
            food.setNonDecaying();
            food.getTraits().add(trait);
            return stack;
        }
        return null;
    }
}
