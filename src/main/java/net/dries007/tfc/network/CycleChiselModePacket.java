/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.player.PlayerData;

public class CycleChiselModePacket
{
    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            PlayerData.get(player).cycleChiselMode();
        }
    }
}
