/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record PourFasterPacket(
    BlockPos pos,
    int slot
)
{
    PourFasterPacket(FriendlyByteBuf buffer)
    {
        this(
            buffer.readBlockPos(),
            buffer.readVarInt()
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(slot);
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            final Level level = player.level();
            if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)
            {
                crucible.setFastPouring(slot);
            }
        }
    }
}
