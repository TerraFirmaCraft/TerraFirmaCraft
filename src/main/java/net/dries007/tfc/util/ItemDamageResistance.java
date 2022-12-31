/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class ItemDamageResistance extends PhysicalDamageTypeData
{
    public static final DataManager<ItemDamageResistance> MANAGER = new DataManager<>(Helpers.identifier("item_damage_resistances"), "item_damage_resistances", ItemDamageResistance::new, ItemDamageResistance::new, ItemDamageResistance::encode, Packet::new);
    public static final IndirectHashCollection<Item, ItemDamageResistance> CACHE = IndirectHashCollection.create(ItemDamageResistance::getValidItems, MANAGER::getValues);

    @Nullable
    public static ItemDamageResistance get(ItemStack item)
    {
        for (ItemDamageResistance resist : CACHE.getAll(item.getItem()))
        {
            if (resist.matches(item))
            {
                return resist;
            }
        }
        return null;
    }

    private final Ingredient ingredient;

    public ItemDamageResistance(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        this.ingredient = Ingredient.fromJson(json.get("ingredient"));
    }

    public ItemDamageResistance(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, buffer);
        ingredient = Ingredient.fromNetwork(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        super.encode(buffer);
        ingredient.toNetwork(buffer);
    }

    public boolean matches(ItemStack item)
    {
        return ingredient.test(item);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public static class Packet extends DataManagerSyncPacket<ItemDamageResistance> {}

}
