/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public final class Fuel extends ItemDefinition
{
    public static final DataManager<Fuel> MANAGER = new DataManager<>(Helpers.identifier("fuels"), "fuel", Fuel::new, Fuel::new, Fuel::encode, Packet::new);
    public static final IndirectHashCollection<Item, Fuel> CACHE = IndirectHashCollection.create(Fuel::getValidItems, MANAGER::getValues);

    @Nullable
    public static Fuel get(ItemStack stack)
    {
        for (Fuel def : CACHE.getAll(stack.getItem()))
        {
            if (def.matches(stack))
            {
                return def;
            }
        }
        return null;
    }

    private final int duration;
    private final float temperature;
    private final float purity;

    public Fuel(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        this.duration = GsonHelper.getAsInt(json, "duration");
        this.temperature = GsonHelper.getAsFloat(json, "temperature");
        this.purity = GsonHelper.getAsFloat(json, "purity", 1f);
    }

    public Fuel(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));

        this.duration = buffer.readVarInt();
        this.temperature = buffer.readFloat();
        this.purity = buffer.readFloat();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);

        buffer.writeVarInt(duration);
        buffer.writeFloat(temperature);
        buffer.writeFloat(purity);
    }

    public int getDuration()
    {
        return duration;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public float getPurity()
    {
        return purity;
    }

    public static class Packet extends DataManagerSyncPacket<Fuel> {}
}
