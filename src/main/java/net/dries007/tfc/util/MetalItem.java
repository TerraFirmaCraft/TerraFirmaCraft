/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.util.collections.IndirectHashCollection;

// todo: slated for removal, if no compelling reason can be found to keep it
public class MetalItem
{
    public static final DataManager<MetalItem> MANAGER = new DataManager.Instance<>(MetalItem::new, "metal_items", "metal item");
    public static final IndirectHashCollection<Item, MetalItem> CACHE = new IndirectHashCollection<>(MetalItem::getValidItems);

    @Nullable
    public static MetalItem get(ItemStack stack)
    {
        for (MetalItem def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return def;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final Metal metal;
    private final int amount;

    public MetalItem(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.ingredient = CraftingHelper.getIngredient(GsonHelper.getAsJsonObject(json, "ingredient"));
        this.metal = JsonHelpers.getFrom(json, "metal", Metal.MANAGER);
        this.amount = GsonHelper.getAsInt(json, "amount");
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Metal getMetal()
    {
        return metal;
    }

    public int getAmount()
    {
        return amount;
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public boolean isValid(ItemStack stack)
    {
        return this.ingredient.test(stack);
    }
}