/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.collections.IndirectHashCollection;

public class Fuel
{
    public static final DataManager<Fuel> MANAGER = new DataManager.Instance<>(Fuel::new, "fuels", "fuel");
    public static final IndirectHashCollection<Item, Fuel> CACHE = new IndirectHashCollection<>(Fuel::getValidItems);

    @Nullable
    public static Fuel get(ItemStack stack)
    {
        for (Fuel def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return def;
            }
        }
        return null;
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        final Fuel fuel = get(stack);
        if (fuel != null)
        {
            // todo: color and convert temperature and duration to words
            text.add(new TranslatableComponent("tfc.tooltip.fuel", fuel.getDuration(), fuel.getTemperature()));
        }
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final int duration;
    private final float temperature;

    public Fuel(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.ingredient = Ingredient.fromJson(JsonHelpers.get(json, "ingredient"));
        this.duration = GsonHelper.getAsInt(json, "duration");
        this.temperature = GsonHelper.getAsFloat(json, "temperature");
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean isValid(ItemStack stack)
    {
        return ingredient.test(stack);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public int getDuration()
    {
        return duration;
    }

    public float getTemperature()
    {
        return temperature;
    }
}
