/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import net.minecraft.block.BlockState;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public class Indicator
{
    public static final Codec<Indicator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.intRange(0, 256).optionalFieldOf("depth", 35).forGetter(c -> c.depth),
        Codec.intRange(0, 15).optionalFieldOf("spread", 15).forGetter(c -> c.spread),
        Codecs.POSITIVE_INT.optionalFieldOf("rarity", 10).forGetter(c -> c.rarity),
        Codecs.weightedCodec(Codecs.LENIENT_BLOCKSTATE, "block").fieldOf("blocks").forGetter(c -> c.states)
    ).apply(instance, Indicator::new));

    private final int depth;
    private final int spread;
    private final int rarity;

    private final IWeighted<BlockState> states;

    public Indicator(int depth, int spread, int rarity, IWeighted<BlockState> states)
    {
        this.depth = depth;
        this.spread = spread;
        this.rarity = rarity;
        this.states = states;
    }

    public BlockState getStateToGenerate(Random random)
    {
        return states.get(random);
    }

    public int getDepth()
    {
        return depth;
    }

    public int getSpread()
    {
        return spread;
    }

    public int getRarity()
    {
        return rarity;
    }
}
