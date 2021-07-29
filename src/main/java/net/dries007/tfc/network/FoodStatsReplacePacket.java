/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.food.TFCFoodStats;

/**
 * A packet that signals to the client it needs to replace the client player's food stats object
 */
public class FoodStatsReplacePacket
{
    void handle(Supplier<NetworkEvent.Context> contextSupplier)
    {
        final NetworkEvent.Context context = contextSupplier.get();
        context.setPacketHandled(true);
        context.enqueueWork(() -> {
            final PlayerEntity player = ClientHelpers.getPlayer();
            if (player != null)
            {
                TFCFoodStats.replaceFoodStats(player);
            }
        });
    }
}
