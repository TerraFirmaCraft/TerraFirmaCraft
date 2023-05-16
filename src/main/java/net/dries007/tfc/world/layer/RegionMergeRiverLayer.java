/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.biome.RegionBiomeSource;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TransformLayer;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.river.MidpointFractal;

public record RegionMergeRiverLayer(RegionGenerator generator) implements TransformLayer
{
    @Override
    public int apply(AreaContext context, Area area, int quartX, int quartZ)
    {
        final int value = area.get(quartX, quartZ);
        if (TFCLayers.hasRiver(value))
        {
            final int gridX = Units.quartToGrid(quartX);
            final int gridZ = Units.quartToGrid(quartZ);

            final float exactGridX = Units.quartToGridExact(quartX);
            final float exactGridZ = Units.quartToGridExact(quartZ);
            final RegionPartition partition = generator.getOrCreatePartition(gridX, gridZ);
            final RegionPartition.Point partitionPoint = partition.get(gridX, gridZ);

            for (MidpointFractal fractal : partitionPoint.rivers())
            {
                // maybeIntersect will skip the more expensive calculation if it fails
                if (fractal.maybeIntersect(exactGridX, exactGridZ, RegionBiomeSource.RIVER_WIDTH) && fractal.intersect(exactGridX, exactGridZ, RegionBiomeSource.RIVER_WIDTH))
                {
                    return TFCLayers.riverFor(value);
                }
            }
        }
        return value;
    }
}