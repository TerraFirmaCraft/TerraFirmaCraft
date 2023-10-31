/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public record Indicator(int depth, int rarity, int undergroundRarity, int undergroundCount, IWeighted<BlockState> states)
{
    public static final Codec<Indicator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.POSITIVE_INT.fieldOf("depth").forGetter(c -> c.depth),
        Codec.INT.fieldOf("rarity").forGetter(c -> c.rarity),
        Codecs.POSITIVE_INT.fieldOf("underground_rarity").forGetter(c -> c.undergroundRarity),
        Codec.INT.fieldOf("underground_count").forGetter(c -> c.undergroundCount),
        Codecs.weightedCodec(Codecs.BLOCK_STATE, "block").fieldOf("blocks").forGetter(c -> c.states)
    ).apply(instance, Indicator::new));

    public BlockState getStateToGenerate(RandomSource random)
    {
        return states.get(random);
    }
}
