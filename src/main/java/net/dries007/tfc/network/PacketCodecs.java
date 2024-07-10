/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;

public interface PacketCodecs
{
    StreamCodec<ByteBuf, ChunkPos> CHUNK_POS = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.x,
        ByteBufCodecs.VAR_INT, c -> c.z,
        ChunkPos::new
    );
}
