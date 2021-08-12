/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public record CaveVegetationConfig(Map<Block, IWeighted<BlockState>> states) implements FeatureConfiguration
{
    public static final Codec<CaveVegetationConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.mapKeyListCodec(Codec.mapPair(
            Codecs.BLOCK.listOf().fieldOf("stone"),
            Codecs.weightedCodec(Codecs.LENIENT_BLOCKSTATE, "block").fieldOf("ore")
        ).codec()).fieldOf("blocks").forGetter(c -> c.states)
    ).apply(instance, CaveVegetationConfig::new));

    @Nullable
    public BlockState getStateToGenerate(BlockState stoneState, Random random)
    {
        final IWeighted<BlockState> weighted = states.get(stoneState.getBlock());
        if (weighted != null)
        {
            return weighted.get(random);
        }
        return null;
    }
}
