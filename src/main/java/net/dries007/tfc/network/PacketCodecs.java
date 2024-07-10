/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public interface PacketCodecs
{
    StreamCodec<ByteBuf, ChunkPos> CHUNK_POS = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.x,
        ByteBufCodecs.VAR_INT, c -> c.z,
        ChunkPos::new
    );

    StreamCodec<RegistryFriendlyByteBuf, Block> BLOCK = ByteBufCodecs.registry(Registries.BLOCK);
    StreamCodec<RegistryFriendlyByteBuf, BlockState> BLOCK_STATE = BLOCK.map(Block::defaultBlockState, BlockBehaviour.BlockStateBase::getBlock);

    static <E extends Enum<E>> StreamCodec<ByteBuf, E> forEnum(Supplier<E[]> values)
    {
        final E[] valuesArray = values.get();
        return ByteBufCodecs.BYTE.map(e -> valuesArray[e], e -> (byte) e.ordinal());
    }
}
