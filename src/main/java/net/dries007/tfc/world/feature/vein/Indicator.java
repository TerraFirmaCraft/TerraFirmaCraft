/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public record Indicator(int depth, int spread, int rarity, IWeighted<BlockState> states)
{
    public static final Codec<Indicator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.POSITIVE_INT.optionalFieldOf("depth", 35).forGetter(c -> c.depth),
        Codec.intRange(0, 15).optionalFieldOf("spread", 15).forGetter(c -> c.spread),
        Codecs.POSITIVE_INT.optionalFieldOf("rarity", 10).forGetter(c -> c.rarity),
        Codecs.weightedCodec(Codecs.BLOCK_STATE, "block").fieldOf("blocks").forGetter(c -> c.states)
    ).apply(instance, Indicator::new));

    public BlockState getStateToGenerate(Random random)
    {
        return states.get(random);
    }
}
