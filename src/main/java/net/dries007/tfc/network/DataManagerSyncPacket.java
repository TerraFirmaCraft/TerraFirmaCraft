/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.util.DataManager;

public abstract class DataManagerSyncPacket<T>
{
    private Map<ResourceLocation, T> elements;

    public DataManagerSyncPacket()
    {
        elements = Collections.emptyMap();
    }

    public DataManagerSyncPacket<T> with(Map<ResourceLocation, T> elements)
    {
        this.elements = elements;
        return this;
    }

    public void encode(DataManager<T> manager, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(elements.size());
        for (Map.Entry<ResourceLocation, T> entry : elements.entrySet())
        {
            buffer.writeResourceLocation(entry.getKey());
            manager.rawToNetwork(buffer, entry.getValue());
        }
    }

    public void decode(DataManager<T> manager, FriendlyByteBuf buffer)
    {
        this.elements = new HashMap<>();
        final int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
        {
            final ResourceLocation id = buffer.readResourceLocation();
            final T element = manager.rawFromNetwork(id, buffer);
            elements.put(id, element);
        }
    }

    public void handle(NetworkEvent.Context context, DataManager<T> manager)
    {
        manager.onSync(context, elements);
    }
}
