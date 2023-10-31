/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.collections.IWeighted;

/**
 * An interface representing a configuration for a given individual vein type. It is implemented as an interface with an accessor for {@link #config()} to play better with composition of codecs.
 */
public interface IVeinConfig extends FeatureConfiguration
{
    /**
     * @return The core {@link VeinConfig} properties for this {@link IVeinConfig}.
     */
    VeinConfig config();

    /**
     * @return The maximum number of chunks away this vein can influence terrain.
     */
    int chunkRadius();

    /**
     * @return A vertical size value used by vein generation to choose a vein y position that does not clip the edge of the range.
     */
    int verticalRadius();

    @Nullable
    default Indicator indicator()
    {
        return config().indicator().orElse(null);
    }

    default int minY()
    {
        return config().minY();
    }

    default int maxY()
    {
        return config().maxY();
    }

    @Nullable
    default BlockState getStateToGenerate(BlockState stoneState, RandomSource random)
    {
        final IWeighted<BlockState> weighted = config().states().get(stoneState.getBlock());
        if (weighted != null)
        {
            return weighted.get(random);
        }
        return null;
    }

    default boolean canSpawnAt(BlockPos pos, Function<BlockPos, Holder<Biome>> biomeQuery)
    {
        return config().biomes().map(tag -> biomeQuery.apply(pos).is(tag)).orElse(true);
    }
}
