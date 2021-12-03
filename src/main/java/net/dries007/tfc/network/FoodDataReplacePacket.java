/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;


import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;

/**
 * A packet that signals to the client it needs to replace the client player's food stats object
 */
public class FoodDataReplacePacket
{
    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final Player player = ClientHelpers.getPlayer();
            if (player != null)
            {
                TFCFoodData.replaceFoodStats(player);
            }
        });
    }
}
