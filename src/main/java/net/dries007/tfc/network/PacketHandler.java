/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;


import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.MutableInt;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import net.dries007.tfc.util.Helpers;

public class PacketHandler
{
    private static final String VERSION = Integer.toString(1);
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(Helpers.identifier("network"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static final MutableInt ID = new MutableInt(0);

    public static void send(PacketDistributor.PacketTarget target, Object message)
    {
        CHANNEL.send(target, message);
    }

    public static void init()
    {
        // Server -> Client
        register(ChunkWatchPacket.class, ChunkWatchPacket::encode, ChunkWatchPacket::new, ChunkWatchPacket::handle);
        register(ChunkUnwatchPacket.class, ChunkUnwatchPacket::encode, ChunkUnwatchPacket::new, ChunkUnwatchPacket::handle);
        register(CalendarUpdatePacket.class, CalendarUpdatePacket::encode, CalendarUpdatePacket::new, CalendarUpdatePacket::handle);
        register(FoodDataReplacePacket.class, FoodDataReplacePacket::new, FoodDataReplacePacket::handle);
        register(FoodDataUpdatePacket.class, FoodDataUpdatePacket::encode, FoodDataUpdatePacket::new, FoodDataUpdatePacket::handle);
        register(PlayerDataUpdatePacket.class, PlayerDataUpdatePacket::encode, PlayerDataUpdatePacket::new, PlayerDataUpdatePacket::handle);
        register(ProspectedPacket.class, ProspectedPacket::encode, ProspectedPacket::new, ProspectedPacket::handle);
        register(ClimateSettingsUpdatePacket.class, ClimateSettingsUpdatePacket::encode, ClimateSettingsUpdatePacket::new, ClimateSettingsUpdatePacket::handle);

        // Client -> Server
        register(SwitchInventoryTabPacket.class, SwitchInventoryTabPacket::encode, SwitchInventoryTabPacket::new, SwitchInventoryTabPacket::handle);
        register(PlaceBlockSpecialPacket.class, PlaceBlockSpecialPacket::new, PlaceBlockSpecialPacket::handle);
        register(ScreenButtonPacket.class, ScreenButtonPacket::encode, ScreenButtonPacket::new, ScreenButtonPacket::handle);
        register(PlayerDrinkPacket.class, PlayerDrinkPacket::new, PlayerDrinkPacket::handle);
    }

    private static <T> void register(Class<T> cls, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, NetworkEvent.Context> handler)
    {
        CHANNEL.registerMessage(ID.getAndIncrement(), cls, encoder, decoder, (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }

    private static <T> void register(Class<T> cls, Supplier<T> factory, BiConsumer<T, NetworkEvent.Context> handler)
    {
        CHANNEL.registerMessage(ID.getAndIncrement(), cls, (packet, buffer) -> {}, buffer -> factory.get(), (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }
}