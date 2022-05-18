/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import org.jetbrains.annotations.Nullable;

public class LacksTraitIngredient extends TraitIngredient
{
    public static LacksTraitIngredient of(@Nullable Ingredient delegate, FoodTrait trait)
    {
        return new LacksTraitIngredient(delegate, trait);
    }

    public static LacksTraitIngredient of(FoodTrait trait)
    {
        return new LacksTraitIngredient(null, trait);
    }

    public LacksTraitIngredient(@Nullable Ingredient delegate, FoodTrait trait)
    {
        super(delegate, trait);
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return super.test(stack) && stack != null && stack.getCapability(FoodCapability.CAPABILITY).map(f -> !f.getTraits().contains(trait)).orElse(false);
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return TraitIngredient.TraitSerializer.LACKS_TRAIT;
    }

    @Nullable
    @Override
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        return stack.getCapability(FoodCapability.CAPABILITY).resolve().map(food -> {
            food.setNonDecaying();
            if (food.getTraits().remove(trait))
            {
                return null;
            }
            return stack;
        }).orElse(null);
    }

}
