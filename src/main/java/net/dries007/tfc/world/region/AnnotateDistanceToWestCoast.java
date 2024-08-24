package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.Mth;

public enum AnnotateDistanceToWestCoast implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;

        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.atIndex(index);
                if (point != null)
                {
                    if (dx == 0)
                    {
                        point.distanceToWestCoast = 0;
                    }
                    else
                    {
                        final Region.Point lastCenterPoint = region.atIndex(index - 1);
                        if (lastCenterPoint == null)
                        {
                            //Sets a little starting bonus when a point in on the eastern border of a cell. The cusp will be smoothed out in the actual rain map
                            point.distanceToWestCoast = point.land() ? (byte) (25 + point.distanceToOcean) : 0;
                        }
                        else
                        {
                            final int lastCenterVal = lastCenterPoint.distanceToWestCoast;
                            if (!point.land())
                            {
                                //Ocean east of a shore will decrease in value faster than the land gains in value, and never below zero
                                point.distanceToWestCoast = (byte) Math.max(lastCenterVal - 2, 0);
                            }
                            else
                            {
                                int sum = 0;
                                //Can adjust the start and end point of this loop arbitrarily, just change the denominator of the average function too
                                for (int dz2 = -2; dz2 <= 1; dz2++)
                                {
                                    final Region.Point lastPoint = region.atOffset(point.index, -1, dz2);
                                    if (lastPoint != null)
                                    {
                                        sum = sum + lastPoint.distanceToWestCoast;
                                    }
                                    else
                                    {
                                        sum = sum + lastCenterVal;
                                    }
                                }
                                point.distanceToWestCoast = (byte) (Mth.ceil(sum / 4f) + 1);
                            }
                        }
                    }
                }
            }
        }

//        //Right now this just draws bright lines at shores!
//        for (final var point : region.points())
//        {
//            if(point.baseLandHeight == 0)
//            {
//                point.distanceToWestCoast = 100;
//            }
//        }
    }
}
