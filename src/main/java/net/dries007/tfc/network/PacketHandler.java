package net.dries007.tfc.network;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import net.dries007.tfc.util.Helpers;

public class PacketHandler
{
    private static final String VERSION = Integer.toString(1);
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(Helpers.identifier("network"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static SimpleChannel get()
    {
        return CHANNEL;
    }

    public static void setup()
    {
        int id = -1;

        CHANNEL.registerMessage(++id, ChunkDataPacket.class, ChunkDataPacket::encode, ChunkDataPacket::new, ChunkDataPacket::handle);
    }
}
