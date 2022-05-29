/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;

public class CycleChiselModePacket
{
    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null)
            {
                player.getCapability(PlayerDataCapability.CAPABILITY).ifPresent(cap -> cap.setChiselMode(cap.getChiselMode().next()));
            }
        });
    }
}
