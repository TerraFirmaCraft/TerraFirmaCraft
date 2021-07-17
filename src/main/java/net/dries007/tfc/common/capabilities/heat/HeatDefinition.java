/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

/**
 * This is a definition (reloaded via {@link HeatManager}) of a heat that is applied to an item stack.
 */
public class HeatDefinition
{
    private final ResourceLocation id;
    private final Supplier<IHeat> capability;
    private final Ingredient ingredient;

    public HeatDefinition(ResourceLocation id, JsonObject obj)
    {
        float heatCapacity = JSONUtils.getAsFloat(obj, "heat_capacity");
        float forgingTemp = JSONUtils.getAsFloat(obj, "forging_temperature", 0);
        float weldingTemp = JSONUtils.getAsFloat(obj, "welding_temperature", 0);

        this.id = id;
        this.ingredient = CraftingHelper.getIngredient(Objects.requireNonNull(obj.get("ingredient")));
        this.capability = () -> new HeatHandler(heatCapacity, forgingTemp, weldingTemp);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    /**
     * Creates a new instance of the capability defined by this object.
     */
    public IHeat create()
    {
        return capability.get();
    }

    public boolean isValid(ItemStack stack)
    {
        return ingredient.test(stack);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }
}
