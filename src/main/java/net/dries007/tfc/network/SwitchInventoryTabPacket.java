/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.common.container.TFCMenuProviders;

public class SwitchInventoryTabPacket
{
    private final Type type;

    public SwitchInventoryTabPacket(Type type)
    {
        this.type = type;
    }

    SwitchInventoryTabPacket(FriendlyByteBuf buffer)
    {
        this.type = Type.VALUES[buffer.readByte()];
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeByte(type.ordinal());
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null)
            {
                player.doCloseContainer();
                if (type == Type.INVENTORY)
                {
                    player.containerMenu = player.inventoryMenu;
                }
                else if (type == Type.CALENDAR)
                {
                    NetworkHooks.openGui(player, TFCMenuProviders.CALENDAR);
                }
                else if (type == Type.NUTRITION)
                {
                    NetworkHooks.openGui(player, TFCMenuProviders.NUTRITION);
                }
                else if (type == Type.CLIMATE)
                {
                    NetworkHooks.openGui(player, TFCMenuProviders.CLIMATE);
                }
                else
                {
                    throw new IllegalStateException("Unknown type?");
                }
            }
        });
    }

    public enum Type
    {
        INVENTORY, CALENDAR, NUTRITION, CLIMATE;

        private static final Type[] VALUES = values();
    }
}