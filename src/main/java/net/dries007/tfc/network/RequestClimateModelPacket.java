package net.dries007.tfc.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;

public class RequestClimateModelPacket
{
    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null)
            {
                final ServerLevel level = player.getLevel();
                final ClimateModel model = Climate.model(level);
                PacketHandler.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClimateModelPacket(model));
            }
        });
    }
}
