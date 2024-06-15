/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.levelgen.DensityFunction;

public record TrilinearInterpolatorList(ChunkNoiseSamplingSettings settings, List<TrilinearInterpolator> interpolators)
{
    public static TrilinearInterpolatorList create(ChunkNoiseSamplingSettings settings)
    {
        return new TrilinearInterpolatorList(settings, new ArrayList<>());
    }

    public TrilinearInterpolator add(DensityFunction function)
    {
        final TrilinearInterpolator interpolator = new TrilinearInterpolator(settings, function);
        interpolators.add(interpolator);
        return interpolator;
    }

    public void initializeForFirstCellX()
    {
        interpolators.forEach(TrilinearInterpolator::initializeForFirstCellX);
    }

    public void advanceCellX(final int cellX)
    {
        interpolators.forEach(i -> i.advanceCellX(cellX));
    }

    public void selectCellYZ(final int cellY, final int cellZ)
    {
        interpolators.forEach(i -> i.selectCellYZ(cellY, cellZ));
    }

    public void updateForXZ(final double x, final double z)
    {
        interpolators.forEach(i -> i.updateForXZ(x, z));
    }

    public void updateForY(final double y)
    {
        interpolators.forEach(i -> i.updateForY(y));
    }

    public void swapSlices()
    {
        interpolators.forEach(TrilinearInterpolator::swapSlices);
    }
}
