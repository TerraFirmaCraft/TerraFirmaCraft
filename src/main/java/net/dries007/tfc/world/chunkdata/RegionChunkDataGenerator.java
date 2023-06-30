/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.settings.RockLayer;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

public class RegionChunkDataGenerator implements ChunkDataGenerator
{
    private final RegionGenerator regionGenerator;

    // todo: rock layer rework, 3d, etc.
    private final ConcurrentArea<RockSettings> bottomRockLayer, middleRockLayer, topRockLayer;
    private final ConcurrentArea<ForestType> forestTypeLayer;

    private final Noise2D layerHeightNoise;
    private final Noise2D forestWeirdnessNoise;
    private final Noise2D forestDensityNoise;

    public RegionChunkDataGenerator(long worldSeed, RockLayerSettings rockLayerSettings, RegionGenerator regionGenerator)
    {
        final RandomSource random = new XoroshiroRandomSource(worldSeed);
        random.setSeed(worldSeed ^ random.nextLong());

        this.regionGenerator = regionGenerator;

        this.bottomRockLayer = ChunkDataGenerator.createRockLayer(random, rockLayerSettings, rockLayerSettings.getRocksForLayer(RockLayer.BOTTOM));
        this.middleRockLayer = ChunkDataGenerator.createRockLayer(random, rockLayerSettings, rockLayerSettings.getRocksForLayer(RockLayer.MIDDLE));
        this.topRockLayer = ChunkDataGenerator.createRockLayer(random, rockLayerSettings, rockLayerSettings.getRocksForLayer(RockLayer.TOP));

        this.layerHeightNoise = new OpenSimplex2D(random.nextInt()).octaves(2).scaled(-10, 10).spread(0.03f);

        // Flora
        forestTypeLayer = new ConcurrentArea<>(TFCLayers.createOverworldForestLayer(random.nextLong(), IArtist.nope()), ForestType::valueOf);
        forestWeirdnessNoise = new OpenSimplex2D(random.nextInt()).octaves(4).spread(0.0025f).map(x -> 1.1f * Math.abs(x)).clamped(0, 1);
        forestDensityNoise = new OpenSimplex2D(random.nextInt()).octaves(4).spread(0.0025f).scaled(-0.2f, 1.2f).clamped(0, 1);
    }


    @Override
    public void generate(ChunkData data)
    {
        final ChunkPos pos = data.getPos();
        final int blockX = pos.getMinBlockX(), blockZ = pos.getMinBlockZ();

        final int gridX = Units.blockToGrid(blockX);
        final int gridZ = Units.blockToGrid(blockZ);

        final Region.Point point00 = regionGenerator.getOrCreateRegionPoint(gridX, gridZ);
        final Region.Point point01 = regionGenerator.getOrCreateRegionPoint(gridX, gridZ + 1);
        final Region.Point point10 = regionGenerator.getOrCreateRegionPoint(gridX + 1, gridZ);
        final Region.Point point11 = regionGenerator.getOrCreateRegionPoint(gridX + 1, gridZ + 1);

        // Distance within the grid of this chunk - so a value between [0, 1] representing the top left of this chunk
        // The interpolator will add 16 / <grid width> to obtain the other side of this chunk, and interpolate from the bounding boxes of the grid points.
        final float deltaX = Units.blockToGridExact(blockX) - gridX;
        final float deltaZ = Units.blockToGridExact(blockZ) - gridZ;

        data.setRainfall(ChunkDataGenerator.sampleInterpolatedGridLayer(point00.rainfall, point01.rainfall, point10.rainfall, point11.rainfall, deltaX, deltaZ));
        data.setAverageTemp(ChunkDataGenerator.sampleInterpolatedGridLayer(point00.temperature, point01.temperature, point10.temperature, point11.temperature, deltaX, deltaZ));

        ChunkDataGenerator.sampleRocksInLayers(data, blockX, blockZ, bottomRockLayer, middleRockLayer, topRockLayer, layerHeightNoise);
        ChunkDataGenerator.sampleForestLayers(data, blockX, blockZ, forestTypeLayer, forestWeirdnessNoise, forestDensityNoise);
    }
}
