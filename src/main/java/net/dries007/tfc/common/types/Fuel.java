package net.dries007.tfc.common.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class Fuel
{
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final int duration;
    private final float temperature;

    public Fuel(ResourceLocation id, JsonObject obj)
    {
        this.id = id;
        this.ingredient = CraftingHelper.getIngredient(JSONUtils.getAsJsonObject(obj, "ingredient"));
        this.duration = JSONUtils.getAsInt(obj, "duration");
        this.temperature = JSONUtils.getAsFloat(obj, "temperature");
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
