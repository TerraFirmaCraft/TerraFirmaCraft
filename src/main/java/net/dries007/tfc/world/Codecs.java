package net.dries007.tfc.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

/**
 * A collection of common codecs that reference vanilla code
 */
public final class Codecs
{
    public static final Codec<Integer> POSITIVE_INT = Codec.intRange(1, Integer.MAX_VALUE);
    public static final Codec<Integer> NONNEGATIVE_INT = Codec.intRange(0, Integer.MAX_VALUE);
    public static final Codec<Float> NONNEGATIVE_FLOAT = Codec.floatRange(0, Float.MAX_VALUE);

    /**
     * A block state which either will accept a simple block state name, or the more complex {"Name": "", "Properties": {}} declaration.
     * In the former case, the default state will be used.
     * When serializing, this will always use the right side, which serializes to the state based codec.
     */
    @SuppressWarnings("deprecation")
    public static final Codec<BlockState> LENIENT_BLOCKSTATE = Codec.either(
        Registry.BLOCK.xmap(Block::defaultBlockState, BlockState::getBlock),
        BlockState.CODEC
    ).xmap(Helpers::resolveEither, Either::right);
}
