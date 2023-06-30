/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.settings.ClimateSettings;
import net.dries007.tfc.world.settings.RockLayerSettings;

public interface BiomeSourceExtension
{
    /**
     * Specialized variant of {@link BiomeSource#getNoiseBiome(int, int, int, Climate.Sampler)}.
     */
    Holder<Biome> getBiome(int quartX, int quartZ);

    BiomeExtension getBiomeExtensionWithRiver(int quartX, int quartZ);

    BiomeExtension getBiomeExtensionNoRiver(int quartX, int quartZ);

    /**
     * Looks up a biome by extension in the biome registry.
     */
    Holder<Biome> getBiomeFromExtension(BiomeExtension extension);

    RegionPartition.Point getPartition(int blockX, int blockZ);

    ChunkDataProvider getChunkDataProvider();

    Settings settings();


    /**
     * @return itself, or the underlying biome provider / source
     */
    default BiomeSource self()
    {
        return (BiomeSource) this;
    }

    default void initRandomState(ServerLevel level) {}

    record Settings(int spawnDistance, int spawnCenterX, int spawnCenterZ, RockLayerSettings rockLayerSettings, ClimateSettings temperatureSettings, ClimateSettings rainfallSettings)
    {
        // todo: PORTING this needs to be re-evaluated. Temperature and rainfall settings need to be integrated better, or maybe removed. It's a bit odd right now
        public static final MapCodec<Settings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("spawn_distance").forGetter(c -> c.spawnDistance),
            Codec.INT.fieldOf("spawn_center_x").forGetter(c -> c.spawnCenterX),
            Codec.INT.fieldOf("spawn_center_z").forGetter(c -> c.spawnCenterZ),
            RockLayerSettings.CODEC.fieldOf("rock_layer_settings").forGetter(c -> c.rockLayerSettings),
            ClimateSettings.CODEC.fieldOf("temperature_settings").forGetter(c -> c.temperatureSettings),
            ClimateSettings.CODEC.fieldOf("rainfall_settings").forGetter(c -> c.rainfallSettings)
        ).apply(instance, Settings::new));
    }
}
