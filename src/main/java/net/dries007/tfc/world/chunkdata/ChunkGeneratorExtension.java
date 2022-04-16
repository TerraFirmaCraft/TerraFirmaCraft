/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;

import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.settings.RockLayerSettings;

/**
 * Identifier interface for the TFC enabled chunk generators
 *
 * Any custom chunk generator wishing to use features from TFC MUST implement this and return a valid chunk data provider
 * This is also used in various places (such as spawn position placement) to identify TFC world generators
 */
public interface ChunkGeneratorExtension
{
    default ChunkDataProvider getChunkDataProvider()
    {
        return getBiomeSource().getChunkDataProvider();
    }

    default RockLayerSettings getRockLayerSettings()
    {
        return getBiomeSource().getRockLayerSettings();
    }

    default BiomeSourceExtension getBiomeSource()
    {
        return (BiomeSourceExtension) self().getBiomeSource();
    }

    Aquifer getOrCreateAquifer(ChunkAccess chunk);

    long getClimateSeed();

    default ChunkGenerator self()
    {
        return (ChunkGenerator) this;
    }
}