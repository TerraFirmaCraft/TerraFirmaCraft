/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.chunkdata.ChunkDataGenerator;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.Settings;

/**
 * Interface for TerraFirmaCraft compatible chunk generators.
 */
public interface ChunkGeneratorExtension
{
    /**
     * Retrieves the {@link ChunkGeneratorExtension} from a structure generator state, if the underlying generator is present and
     * is a TFC compatible chunk generator. This is set in {@link #initRandomState(ChunkMap, ServerLevel)} in the individual generator,
     * by caching it through the {@link net.minecraft.world.level.levelgen.RandomState}.
     * @param state The chunk generator structure state.
     * @return The underlying chunk generator.
     */
    static @Nullable ChunkGeneratorExtension getFromStructureState(ChunkGeneratorStructureState state)
    {
        return ((RandomStateExtension) (Object) state.randomState()).tfc$getChunkGeneratorExtension();
    }

    /**
     * @return The world generator settings.
     */
    Settings settings();

    /**
     * @return The rock layer settings.
     */
    default RockLayerSettings rockLayerSettings()
    {
        return settings().rockLayerSettings();
    }

    /**
     * Used on client to set the settings via the preset configuration screen.
     * This is technically compatible with any {@link ChunkGeneratorExtension} but will only exist if it is registered via {@link RegisterPresetEditorsEvent} for that screen.
     */
    void applySettings(UnaryOperator<Settings> settings);

    ChunkDataGenerator chunkDataGenerator();

    Aquifer getOrCreateAquifer(ChunkAccess chunk);

    /**
     * Find the spawn biome. This is by default a bouncer to {@link BiomeSourceExtension#findSpawnBiome(Settings, RandomSource)}, which uses the {@link #settings()} from the chunk generator.
     */
    default BlockPos findSpawnBiome(RandomSource random)
    {
        return ((BiomeSourceExtension) self().getBiomeSource()).findSpawnBiome(settings(), random);
    }

    /**
     * Called from the initialization of {@link ChunkMap}, to initialize seed-based properties on any chunk generator implementing {@link ChunkGeneratorExtension}.
     */
    void initRandomState(ChunkMap chunkMap, ServerLevel level);

    default ChunkGenerator self()
    {
        return (ChunkGenerator) this;
    }
}