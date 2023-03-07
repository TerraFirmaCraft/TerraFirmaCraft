/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;

public class PourFasterPacket
{
    private final BlockPos pos;
    private final int slot;

    public PourFasterPacket(BlockPos pos, int slot)
    {
        this.pos = pos;
        this.slot = slot;
    }

    PourFasterPacket(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();
        slot = buffer.readVarInt();
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final ServerPlayer sender = context.getSender();
            if (sender != null)
            {
                final Level level = sender.level;
                if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)
                {
                    crucible.setFastPouring(slot);
                }
            }
        });
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(slot);
    }
}
