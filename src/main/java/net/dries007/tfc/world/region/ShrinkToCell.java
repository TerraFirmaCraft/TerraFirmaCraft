/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

public enum ShrinkToCell implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;

        final int modifiedSizeX = 1 + context.maxX - context.minX;
        final int modifiedSizeZ = 1 + context.maxZ - context.minZ;

        final Region.Point[] modifiedPoints = new Region.Point[modifiedSizeX * modifiedSizeZ];
        final int offsetX = context.minX - region.minX();
        final int offsetZ = context.minZ - region.minZ();

        final int prevSizeX = region.sizeX();

        for (int dx = 0; dx < modifiedSizeX; dx++)
        {
            for (int dz = 0; dz < modifiedSizeZ; dz++)
            {
                modifiedPoints[dx + modifiedSizeX * dz] = region.data()[(offsetX + dx) + prevSizeX * (offsetZ + dz)];
            }
        }

        context.region.setRegionArea(modifiedPoints, context.minX, context.minZ, context.maxX, context.maxZ);
    }
}
