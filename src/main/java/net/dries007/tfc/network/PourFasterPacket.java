/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record PourFasterPacket(
    BlockPos pos,
    int slot
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PourFasterPacket> TYPE = PacketHandler.type("pour_faster");
    public static final StreamCodec<ByteBuf, PourFasterPacket> STREAM = StreamCodec.composite(
        BlockPos.STREAM_CODEC, c -> c.pos,
        ByteBufCodecs.VAR_INT, c -> c.slot,
        PourFasterPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
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
