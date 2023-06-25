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
        final int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ();

        final int gridTopLeftX = Units.blockToGrid(chunkX);
        final int gridTopLeftZ = Units.blockToGrid(chunkZ);

        // Two levels of interpolation:
        // One at grid level, between the four containing points of this chunk
        // Then we generate four points at the corners of this chunk, to be saved to chunk data

        final Region.Point pointNW = regionGenerator.getOrCreateRegionPoint(gridTopLeftX, gridTopLeftZ);
        final Region.Point pointNE = regionGenerator.getOrCreateRegionPoint(gridTopLeftX + 1, gridTopLeftZ);
        final Region.Point pointSW = regionGenerator.getOrCreateRegionPoint(gridTopLeftX, gridTopLeftZ + 1);
        final Region.Point pointSE = regionGenerator.getOrCreateRegionPoint(gridTopLeftX + 1, gridTopLeftZ + 1);

        final LerpFloatLayer gridRainfallLayer = new LerpFloatLayer(pointNW.rainfall, pointNE.rainfall, pointSW.rainfall, pointSE.rainfall);
        final LerpFloatLayer gridTemperatureLayer = new LerpFloatLayer(pointNW.temperature, pointNE.temperature, pointSW.temperature, pointSE.temperature);

        data.setRainfall(ChunkDataGenerator.sampleInterpolatedGridLayer(chunkX, chunkZ, gridRainfallLayer));
        data.setAverageTemp(ChunkDataGenerator.sampleInterpolatedGridLayer(chunkX, chunkZ, gridTemperatureLayer));

        ChunkDataGenerator.sampleRocksInLayers(data, chunkX, chunkZ, bottomRockLayer, middleRockLayer, topRockLayer, layerHeightNoise);
        ChunkDataGenerator.sampleForestLayers(data, chunkX, chunkZ, forestTypeLayer, forestWeirdnessNoise, forestDensityNoise);
    }
}
