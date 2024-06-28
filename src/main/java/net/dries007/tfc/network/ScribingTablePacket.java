/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.container.ScribingTableContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record ScribingTablePacket(String name)
{
    ScribingTablePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readUtf());
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(name);
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.containerMenu instanceof ScribingTableContainer container)
        {
            container.setItemName(name);
        }
    }
}
