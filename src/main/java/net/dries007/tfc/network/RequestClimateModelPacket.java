/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;

public class RequestClimateModelPacket
{
    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            final ServerLevel level = player.serverLevel();
            final ClimateModel model = Climate.model(level);
            PacketHandler.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClimateModelPacket(model));
        }
    }
}
