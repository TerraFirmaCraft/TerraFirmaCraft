/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.settings.ClimateSettings;

public class ClimateSettingsUpdatePacket
{
    private final ClimateSettings settings;

    public ClimateSettingsUpdatePacket(ClimateSettings settings)
    {
        this.settings = settings;
    }

    ClimateSettingsUpdatePacket(FriendlyByteBuf buffer)
    {
        final float lo = buffer.readFloat();
        final float hi = buffer.readFloat();
        final int scale = buffer.readInt();
        final boolean endless = buffer.readBoolean();

        this.settings = new ClimateSettings(lo, hi, scale, endless);
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(settings.lowThreshold());
        buffer.writeFloat(settings.highThreshold());
        buffer.writeInt(settings.scale());
        buffer.writeBoolean(settings.endlessPoles());
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final Level level = ClientHelpers.getLevel();
            if (level != null)
            {
                Climate.onWorldLoad(level, settings);
            }
        });
    }
}
