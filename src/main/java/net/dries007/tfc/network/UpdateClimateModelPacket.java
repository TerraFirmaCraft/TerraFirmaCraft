/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.tracker.WorldTracker;

public record UpdateClimateModelPacket(ClimateModel model)
{
    static UpdateClimateModelPacket decode(FriendlyByteBuf buffer)
    {
        final ClimateModel model = Climate.create(buffer.readResourceLocation());
        model.onReceiveOnClient(buffer);
        return new UpdateClimateModelPacket(model);
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(Climate.getId(model));
        model.onSyncToClient(buffer);
    }

    void handle()
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            WorldTracker.get(level).setClimateModel(model);
        }
    }
}
