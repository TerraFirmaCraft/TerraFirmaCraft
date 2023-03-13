/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.river.Watershed;
import net.dries007.tfc.world.settings.ClimateSettings;
import net.dries007.tfc.world.settings.RockLayerSettings;

public interface BiomeSourceExtension
{
    Holder<Biome> getNoiseBiome(int quartX, int quartZ);

    BiomeExtension getNoiseBiomeVariants(int quartX, int quartZ);

    Holder<Biome> getBiome(BiomeExtension variants);

    int getSpawnDistance();

    int getSpawnCenterX();

    int getSpawnCenterZ();

    ChunkDataProvider getChunkDataProvider();

    RockLayerSettings getRockLayerSettings();

    ClimateSettings getTemperatureSettings();

    /** This is nullable for backwards compat reasons, although implementations <strong>should</strong> provide this. */
    @Nullable
    default Watershed.Context getWatersheds()
    {
        return null;
    }

    /**
     * @return itself, or the underlying biome provider / source
     */
    default BiomeSource self()
    {
        return (BiomeSource) this;
    }
}
