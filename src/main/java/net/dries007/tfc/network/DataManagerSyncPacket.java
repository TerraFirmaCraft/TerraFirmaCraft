/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mojang.logging.LogUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.DataManagers;

public record DataManagerSyncPacket(List<Entry<?>> values) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<DataManagerSyncPacket> TYPE = PacketHandler.type("data_managers");
    public static final StreamCodec<RegistryFriendlyByteBuf, DataManagerSyncPacket> CODEC = ByteBufCodecs.registry(DataManagers.KEY)
        .<Entry<?>>dispatch(Entry::manager, DataManagerSyncPacket::streamCodec)
        .apply(ByteBufCodecs.list())
        .map(DataManagerSyncPacket::new, DataManagerSyncPacket::values);

    private static final Logger LOGGER = LogUtils.getLogger();

    private static <T> StreamCodec<RegistryFriendlyByteBuf, Entry<T>> streamCodec(DataManager<T> manager)
    {
        return ByteBufCodecs.<RegistryFriendlyByteBuf, ResourceLocation, T, Map<ResourceLocation, T>>map(HashMap::new, ResourceLocation.STREAM_CODEC, manager.streamCodec())
            .map(e -> new Entry<>(manager, e), e -> e.values);
    }

    public DataManagerSyncPacket()
    {
        this(DataManagers.REGISTRY.stream().filter(DataManager::isSynced).<Entry<?>>map(Entry::new).toList());
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(boolean isMemoryConnection)
    {
        for (Entry<?> v : values)
            v.handle(isMemoryConnection);
    }

    record Entry<T>(
        DataManager<T> manager,
        Map<ResourceLocation, T> values
    ) {
        Entry(DataManager<T> manager)
        {
            this(manager, manager.getElements());
        }

        void handle(boolean isMemoryConnection)
        {
            if (isMemoryConnection)
            {
                LOGGER.info("Ignoring DataManager sync on logical server");
            }
            else
            {
                manager.onSync(values);
            }
        }
    }
}
