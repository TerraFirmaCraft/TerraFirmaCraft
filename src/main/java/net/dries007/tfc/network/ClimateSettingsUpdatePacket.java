package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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
        final float f1 = buffer.readFloat();
        final float f2 = buffer.readFloat();
        final float f3 = buffer.readFloat();
        final float f4 = buffer.readFloat();
        final int scale = buffer.readInt();
        final boolean endless = buffer.readBoolean();

        this.settings = new ClimateSettings(f1, f2, f3, f4, scale, endless);
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(settings.firstMax());
        buffer.writeFloat(settings.secondMax());
        buffer.writeFloat(settings.thirdMax());
        buffer.writeFloat(settings.fourthMax());
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
