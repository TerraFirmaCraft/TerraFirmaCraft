/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.Random;
import net.minecraft.world.level.levelgen.RandomSource;

import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;

public final class PLayers
{
    public static TypedAreaFactory<Region.Point> createLayers(long seed)
    {
        final RegionGenerator generator = new RegionGenerator(seed);

        TypedAreaFactory<Region.Point> pointLayer;

        pointLayer = new RegionLayer(generator).apply(seed);

        pointLayer = TypedZoomLayer.<Region.Point>normal().apply(seed, pointLayer);
        pointLayer = TypedZoomLayer.<Region.Point>normal().apply(seed, pointLayer);
        pointLayer = TypedZoomLayer.<Region.Point>normal().apply(seed, pointLayer);

        // Each point is a single chunk

        return pointLayer;
    }

    public static AreaFactory createBiomeLayer(RegionGenerator generator, long seed)
    {
        final Random random = new Random(seed);
        final TypedAreaFactory<Region.Point> regionLayer = new RegionLayer(generator).apply(random.nextLong());

        AreaFactory mainLayer;

        mainLayer = RegionBiomeLayer.INSTANCE.apply(regionLayer);

        // Grid scale

        mainLayer = RegionEdgeBiomeLayer.INSTANCE.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);

        mainLayer = ShoreLayer.INSTANCE.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);

        // Chunk scale

        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);

        // Quart scale

        mainLayer = SmoothLayer.INSTANCE.apply(random.nextLong(), mainLayer);

        return mainLayer;
    }

    public static AreaFactory createUniformLayer(RandomSource random, int zoomLevels)
    {
        AreaFactory layer;

        layer = UniformLayer.INSTANCE.apply(random.nextLong());
        for (int i = 0; i < zoomLevels; i++)
        {
            layer = ZoomLayer.NORMAL.apply(random.nextLong(), layer);
        }
        layer = SmoothLayer.INSTANCE.apply(random.nextLong(), layer);

        return layer;
    }
}
