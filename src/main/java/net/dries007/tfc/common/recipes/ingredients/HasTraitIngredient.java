/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import org.jetbrains.annotations.Nullable;

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
        return super.test(stack) && stack != null && stack.getCapability(FoodCapability.CAPABILITY).map(f -> f.getTraits().contains(trait)).orElse(false);
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return TraitSerializer.HAS_TRAIT;
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(TraitSerializer.HAS_TRAIT).toString());
        super.toJson().getAsJsonObject().entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
        return json;
    }

    @Nullable
    @Override
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        return stack.getCapability(FoodCapability.CAPABILITY).map(food -> {
            food.setNonDecaying();
            food.getTraits().add(trait);
            return stack;
        }).orElse(null);
    }
}
