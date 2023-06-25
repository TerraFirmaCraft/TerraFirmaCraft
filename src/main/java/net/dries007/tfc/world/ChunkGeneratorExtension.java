/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;

import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.settings.RockLayerSettings;

/**
 * Identifier interface for the TFC enabled chunk generators
 * <p>
 * Any custom chunk generator wishing to use features from TFC MUST implement this and return a valid chunk data provider
 * This is also used in various places (such as spawn position placement) to identify TFC world generators
 */
public interface ChunkGeneratorExtension
{
    default ChunkDataProvider getChunkDataProvider()
    {
        return getBiomeSourceExtension().getChunkDataProvider();
    }

    default RockLayerSettings getRockLayerSettings()
    {
        return getBiomeSourceExtension().settings().rockLayerSettings();
    }

    default BiomeSourceExtension getBiomeSourceExtension()
    {
        return (BiomeSourceExtension) self().getBiomeSource();
    }

    Aquifer getOrCreateAquifer(ChunkAccess chunk);

    /**
     * Called from the initialization of {@link net.minecraft.server.level.ChunkMap}, to initialize seed-based properties.
     */
    void initRandomState(ServerLevel level);

    default ChunkGenerator self()
    {
        return (ChunkGenerator) this;
    }
}