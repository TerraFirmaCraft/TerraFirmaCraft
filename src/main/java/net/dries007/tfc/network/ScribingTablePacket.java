/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.common.container.ScribingTableContainer;

public class ScribingTablePacket
{
    private final String name;

    public ScribingTablePacket(String name)
    {
        this.name = name;
    }

    ScribingTablePacket(FriendlyByteBuf buffer)
    {
        name = buffer.readUtf();
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(name);
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null && sender.containerMenu instanceof ScribingTableContainer container)
            {
                container.setItemName(name);
            }
        });
    }
}
