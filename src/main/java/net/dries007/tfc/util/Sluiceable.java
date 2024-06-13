/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class Sluiceable extends ItemDefinition
{
    public static final DataManager<Sluiceable> MANAGER = new DataManager<>(Helpers.identifier("sluicing"), "sluicing", Sluiceable::new, Sluiceable::new, Sluiceable::encode, Sluiceable.Packet::new);
    public static final IndirectHashCollection<Item, Sluiceable> CACHE = IndirectHashCollection.create(Sluiceable::getValidItems, MANAGER::getValues);

    @Nullable
    public static Sluiceable get(ItemStack item)
    {
        for (Sluiceable sluiceable : CACHE.getAll(item.getItem()))
        {
            if (sluiceable.matches(item))
            {
                return sluiceable;
            }
        }
        return null;
    }

    private final ResourceLocation lootTable;

    public Sluiceable(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        lootTable = JsonHelpers.getResourceLocation(json, "loot_table");
    }

    public Sluiceable(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));
        lootTable = buffer.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        buffer.writeUtf(lootTable.toString());
    }

    public ResourceLocation getLootTable()
    {
        return lootTable;
    }

    public static class Packet extends DataManagerSyncPacket<Sluiceable> {}
}
