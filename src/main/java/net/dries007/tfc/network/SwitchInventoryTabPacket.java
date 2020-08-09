/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.objects.container.TFCContainerProviders;

public class SwitchInventoryTabPacket
{
    private final Type type;

    public SwitchInventoryTabPacket(Type type)
    {
        this.type = type;
    }

    SwitchInventoryTabPacket(PacketBuffer buffer)
    {
        this.type = Type.VALUES[buffer.readByte()];
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeByte(type.ordinal());
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().setPacketHandled(true);
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null)
            {
                player.closeContainer();
                if (type == Type.INVENTORY)
                {
                    player.openContainer = player.container;
                }
                else if (type == Type.CALENDAR)
                {
                    NetworkHooks.openGui(player, TFCContainerProviders.CALENDAR);
                }
                else if (type == Type.NUTRITION)
                {
                    NetworkHooks.openGui(player, TFCContainerProviders.NUTRITION);
                }
            }
        });
    }

    public enum Type
    {
        INVENTORY, CALENDAR, NUTRITION;

        private static final Type[] VALUES = values();
    }
}
