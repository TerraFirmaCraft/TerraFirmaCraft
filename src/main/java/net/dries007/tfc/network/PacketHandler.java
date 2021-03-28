/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import net.dries007.tfc.util.Helpers;

public class PacketHandler
{
    private static final String VERSION = Integer.toString(1);
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(Helpers.identifier("network"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void send(PacketDistributor.PacketTarget target, Object message)
    {
        CHANNEL.send(target, message);
    }

    public static SimpleChannel get()
    {
        return CHANNEL;
    }

    @SuppressWarnings("UnusedAssignment")
    public static void init()
    {
        int id = 0;

        CHANNEL.registerMessage(id++, ChunkWatchPacket.class, ChunkWatchPacket::encode, ChunkWatchPacket::new, ChunkWatchPacket::handle);
        CHANNEL.registerMessage(id++, ChunkUnwatchPacket.class, ChunkUnwatchPacket::encode, ChunkUnwatchPacket::new, ChunkUnwatchPacket::handle);
        CHANNEL.registerMessage(id++, CalendarUpdatePacket.class, CalendarUpdatePacket::encode, CalendarUpdatePacket::new, CalendarUpdatePacket::handle);
        CHANNEL.registerMessage(id++, SwitchInventoryTabPacket.class, SwitchInventoryTabPacket::encode, SwitchInventoryTabPacket::new, SwitchInventoryTabPacket::handle);
        CHANNEL.registerMessage(id++, PlaceBlockSpecialPacket.class, (packet, buf) -> {}, buffer -> new PlaceBlockSpecialPacket(), PlaceBlockSpecialPacket::handle);
    }
}