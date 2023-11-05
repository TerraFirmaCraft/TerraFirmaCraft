/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.region.ChooseRocks;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

public record RegionChunkDataGenerator(
    RegionGenerator regionGenerator,
    RockLayerSettings rockLayerSettings,
    ConcurrentArea<ForestType> forestTypeLayer,
    ThreadLocal<Area> rockLayerArea,
    Noise2D layerHeightNoise,
    Noise2D layerSkewXNoise,
    Noise2D layerSkewZNoise,
    Noise2D forestWeirdnessNoise,
    Noise2D forestDensityNoise
) implements ChunkDataGenerator
{
    private static final int LAYER_OFFSET_BITS = 3;
    private static final int LAYER_OFFSET_MASK = (1 << LAYER_OFFSET_BITS) - 1;
    private static final int[] LAYER_OFFSETS = new int[1 << (LAYER_OFFSET_BITS + 1)];

    private static final float DELTA_Y_OFFSET = 12;

    static
    {
        final RandomSource random = new XoroshiroRandomSource(1923874192341L);
        for (int i = 0; i < LAYER_OFFSETS.length; i++)
        {
            LAYER_OFFSETS[i] = random.nextInt(0, 100_000);
        }
    }

    private static int getOffsetX(int layer)
    {
        return LAYER_OFFSETS[(layer & LAYER_OFFSET_MASK) << 1];
    }

    private static int getOffsetZ(int layer)
    {
        return LAYER_OFFSETS[((layer & LAYER_OFFSET_MASK) << 1) | 0b1];
    }

    public static RegionChunkDataGenerator create(long worldSeed, RockLayerSettings rockLayerSettings, RegionGenerator regionGenerator)
    {
        final RandomSource random = new XoroshiroRandomSource(worldSeed);
        random.setSeed(worldSeed ^ random.nextLong());

        final ThreadLocal<Area> rockLayerArea = ThreadLocal.withInitial(TFCLayers.createOverworldRockLayer(regionGenerator, random.nextLong()));
        final Noise2D layerHeightNoise = new OpenSimplex2D(random.nextInt()).octaves(3).scaled(43, 63).spread(0.014f);
        final Noise2D layerSkewXNoise = new OpenSimplex2D(random.nextInt()).octaves(2).scaled(-1.8f, 1.8f).spread(0.01f);
        final Noise2D layerSkewZNoise = new OpenSimplex2D(random.nextInt()).octaves(2).scaled(-1.8f, 1.8f).spread(0.01f);

        // Flora
        final ConcurrentArea<ForestType> forestTypeLayer = new ConcurrentArea<>(TFCLayers.createOverworldForestLayer(random.nextLong(), IArtist.nope()), ForestType::valueOf);
        final Noise2D forestWeirdnessNoise = new OpenSimplex2D(random.nextInt()).octaves(4).spread(0.0025f).map(x -> 1.1f * Math.abs(x)).clamped(0, 1);
        final Noise2D forestDensityNoise = new OpenSimplex2D(random.nextInt()).octaves(4).spread(0.0025f).scaled(-0.2f, 1.2f).clamped(0, 1);

        return new RegionChunkDataGenerator(regionGenerator, rockLayerSettings, forestTypeLayer, rockLayerArea, layerHeightNoise, layerSkewXNoise, layerSkewZNoise, forestWeirdnessNoise, forestDensityNoise);
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
        final double deltaX = Units.blockToGridExact(blockX) - gridX;
        final double deltaZ = Units.blockToGridExact(blockZ) - gridZ;

        final LerpFloatLayer rainfallLayer = ChunkDataGenerator.sampleInterpolatedGridLayer(point00.rainfall, point01.rainfall, point10.rainfall, point11.rainfall, deltaX, deltaZ);
        final LerpFloatLayer temperatureLayer = ChunkDataGenerator.sampleInterpolatedGridLayer(point00.temperature, point01.temperature, point10.temperature, point11.temperature, deltaX, deltaZ);

        // This layer is sampled per-chunk, to avoid the waste of two additional zoom layers
        final ForestType forestType = forestTypeLayer.get(blockX >> 4, blockZ >> 4);
        final float forestWeirdness = (float) forestWeirdnessNoise.noise(blockX + 8, blockZ + 8);
        final float forestDensity = (float) forestDensityNoise.noise(blockX + 8, blockZ + 8);

        data.generatePartial(
            rainfallLayer,
            temperatureLayer,
            forestType,
            forestWeirdness,
            forestDensity
        );
    }

    @Override
    public RockSettings generateRock(int x, int y, int z, int surfaceY, @Nullable ChunkRockDataCache cache)
    {
        return generateRock(x, y, z, surfaceY, cache, null);
    }

    @Override
    public void displayDebugInfo(List<String> tooltip, BlockPos pos, int surfaceY)
    {
        generateRock(pos.getX(), pos.getY(), pos.getZ(), surfaceY, null, tooltip);
    }

    @SuppressWarnings("deprecation")
    private RockSettings generateRock(int x, int y, int z, int surfaceY, @Nullable ChunkRockDataCache cache, @Nullable List<String> tooltip)
    {
        // Adjust surface Y so that really high mountains don't pull up the rock layers too much
        float adjustedSurfaceY = surfaceY;
        if (adjustedSurfaceY > 125)
        {
            adjustedSurfaceY = 125 + 0.3f * (surfaceY - 125);
        }

        // Iterate downwards to find the nth layer
        int layer = 0;
        float deltaY = adjustedSurfaceY - y;
        float layerHeight;
        do
        {
            if (cache != null)
            {
                populateLayerInCache(cache, layer);
                layerHeight = cache.getLayerHeight(layer, x, z);
            }
            else
            {
                final int layerX = x + getOffsetX(layer);
                final int layerZ = z + getOffsetZ(layer);

                layerHeight = (float) layerHeightNoise.noise(layerX, layerZ);
            }
            if (deltaY <= layerHeight)
            {
                break;
            }
            deltaY -= layerHeight;
            layer++;
        } while (deltaY > 0);

        final int offsetX, offsetZ;
        final float skewNoiseX, skewNoiseZ;

        if (cache != null)
        {
            // Unused, since we query cached skews directly
            offsetX = offsetZ = 0;

            // The cache is required to be populated as before as we iterated layers, we also populate the skew noise there
            skewNoiseX = cache.getLayerSkewX(layer, x, z);
            skewNoiseZ = cache.getLayerSkewZ(layer, x, z);
        }
        else
        {
            // Layer count (from surface) is now known
            // Sample (lateral) offset
            offsetX = x + getOffsetX(layer);
            offsetZ = z + getOffsetZ(layer);

            // Skew position after calculating the correct layer offset, and then skewing by deltaY
            skewNoiseX = (float) layerSkewXNoise.noise(offsetX, offsetZ);
            skewNoiseZ = (float) layerSkewZNoise.noise(offsetX, offsetZ);
        }

        final int skewX = x + (int) (skewNoiseX * (deltaY + DELTA_Y_OFFSET));
        final int skewZ = z + (int) (skewNoiseZ * (deltaY + DELTA_Y_OFFSET));

        // Rock seed (including type and seed) at this point in the layer
        final int point = rockLayerArea.get().get(skewX, skewZ);

        // Sample the rock at this layer, progressing downwards according to the possible layered rocks
        final RockSettings rock = rockLayerSettings.sampleAtLayer(point, layer);

        if (tooltip != null)
        {
            tooltip.add("Pos: %d, %d, %d S: %d dY: %.1f Layer: %d LayerH: %.1f".formatted(x, y, z, surfaceY, deltaY, layer, layerHeight));
            tooltip.add("Offset: %d, %d Skew: %.1f, %.1f / %d, %d Seed: %d Type: %d".formatted(offsetX, offsetZ, skewNoiseX, skewNoiseZ, skewX, skewZ, point >> ChooseRocks.TYPE_BITS, point & ChooseRocks.TYPE_MASK));
            tooltip.add("Rock: %s".formatted(BuiltInRegistries.BLOCK.getKey(rock.raw())));
        }

        return rock;
    }

    private void populateLayerInCache(ChunkRockDataCache cache, int layer)
    {
        if (cache.layers() <= layer)
        {
            // Populate layers of layer height, and skew noise here
            final int chunkX = cache.pos().getMinBlockX(), chunkZ = cache.pos().getMinBlockZ();
            for (int populateLayer = cache.layers(); populateLayer <= layer; populateLayer++)
            {
                final float[] populatedLayerHeight = new float[16 * 16];
                final float[] populatedLayerSkew = new float[16 * 16 * 2];
                final int layerX = chunkX + getOffsetX(layer);
                final int layerZ = chunkZ + getOffsetZ(layer);
                for (int dx = 0; dx < 16; dx++)
                {
                    for (int dz = 0; dz < 16; dz++)
                    {
                        final int offsetX = layerX + dx, offsetZ = layerZ + dz;
                        final int i = Units.index(dx, dz);

                        populatedLayerHeight[i] = (float) layerHeightNoise.noise(offsetX, offsetZ);
                        populatedLayerSkew[i << 1] = (float) layerSkewXNoise.noise(offsetX, offsetZ);
                        populatedLayerSkew[(i << 1) | 0b1] = (float) layerSkewZNoise.noise(offsetX, offsetZ);
                    }
                }
                cache.addLayer(populatedLayerHeight, populatedLayerSkew);
            }
        }
    }
}
