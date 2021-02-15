/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

/**
 * Marker for TFC biome providers
 */
public interface ITFCBiomeProvider
{
    int getSpawnDistance();

    int getSpawnCenterX();

    int getSpawnCenterZ();

    /**
     * An optional implementation, see {  TFCBiomeProvider}
     */
    @Nullable
    default BlockPos findBiomeIgnoreClimate(int x, int y, int z, int radius, int increment, Predicate<Biome> predicate, Random rand)
    {
        return biomeSource().findBiomePosition(x, y, z, radius, increment, predicate, rand, false);
    }

    /**
     * @return itself, or the underlying biome provider / source
     */
    default BiomeProvider biomeSource()
    {
        return (BiomeProvider) this;
    }
}
