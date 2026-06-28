/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public enum AddMountainsAndBarrierIslands implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final RandomSource random = context.random;

        // Add collisional ranges first
        for (Region.Point point : region.points())
        {
            if (point.land() && point.divergence < 0 && point.distanceToEdge < 1.5 * context.generator().continentNoise.noise(point.x, point.z) - 5.2)
            {
                point.setMountain();
            }
        }

        // Then ranges following contours
        for (int attempt = 0, mtnsPlaced = 0, islesPlaced = 0; attempt < 40 && !(mtnsPlaced >= 3 && islesPlaced >= 6); attempt++)
        {
            final @Nullable Region.Point origin = region.random(random);
            if (origin != null)
            {
                // Attempt to construct a mountain range
                // We do this with a bit of a DFS / BFS hybrid - intentionally imprecise and random - across a contour of the base land height
                // Ranges at low altitudes (near ocean) get marked as oceanic ranges, where mid-high altitude ranges get marked as high altitude mountains.
                if (mtnsPlaced < 3 && (origin.land() && origin.baseLandHeight <= 1 || (origin.baseLandHeight >= 4 && origin.baseLandHeight <= 11)))
                {
                    final IntSet range = placeRange(region, random, origin.index);
                    if (range.size() > 45)
                    {
                        range.forEach(index -> {
                            final Region.Point point = region.atIndex(index);

                            point.setMountain();
                            if (origin.divergence < 0 && origin.baseLandHeight < 8)
                            {
                                // Mark mountain chain as volcanic if we are in a subduction zone and not too far inland
                                point.setVolcanic();
                            }
                            if (origin.baseLandHeight <= 2)
                            {
                                point.setCoastalMountain();
                            }
                        });
                        mtnsPlaced++;
                    }
                }
                // Attempt to construct a volcanic arc on the continental shelf in subduction zones
                // Works similarly to mountain ranges, but based on distance to edge of continental shelf
                else if (islesPlaced < 6 && !origin.land() && origin.oceanDepth == 2 && origin.divergence < 0 && origin.distanceToDeepOcean <= 3 && origin.distanceToLand > 2)
                {
                    final IntSet range = placeVolcanicArc(region, random, origin.index);
                    if (range.size() > 45)
                    {
                        final byte originContour = origin.distanceToDeepOcean;
                        range.forEach(index -> {
                            final Region.Point point = region.atIndex(index);

                            if (point.distanceToDeepOcean >= originContour && point.distanceToDeepOcean <= originContour + 1)
                            {
                                point.setBarrierIsland();
                            }
                            point.setVolcanic();
                            point.oceanDepth = 1;
                        });
                        islesPlaced = islesPlaced + 2;
                    }
                }
                // Attempt to construct a barrier island chain
                // Works similarly to mountain ranges, but based on distance to land and are thinner
                else if (islesPlaced < 6 && !origin.land() && origin.oceanDepth == 2 && origin.divergence > 0 && origin.distanceToLand > 2 && origin.distanceToLand < 6)
                {
                    final IntSet range = placeBarrier(region, random, origin.index);
                    final byte startContour = (byte) Math.max(1, origin.distanceToLand);
                    if (range.size() > 45)
                    {
                        range.forEach(index -> {
                            final Region.Point point = region.atIndex(index);

                            if (point.distanceToLand == startContour)
                            {
                                point.setBarrierIsland();
                            }
                            point.oceanDepth = 1;
                        });
                        islesPlaced++;
                    }
                }
            }
        }
    }

    private IntSet placeRange(Region region, RandomSource random, int originIndex)
    {
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        final IntSet range = new IntOpenHashSet();

        queue.enqueue(originIndex);
        explored.set(originIndex);
        range.add(originIndex);

        // So that low altitude ranges don't start at 0 altitude, now they can follow the [0, 1] contour
        final int originBaseLandHeight = Math.max(1, region.atIndex(originIndex).baseLandHeight);
        final int maxSize = 70 + random.nextInt(40);

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            if (range.size() > maxSize)
            {
                break;
            }

            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);

                    // Only explore the contour within [-1, 1] of the origin
                    // The baseLandHeight > 2 || distanceToOcean < 3 is to avoid what should be coastal mountains diverting inland due to
                    // the presence of a cell edge causing an artificial low point.
                    if (point != null &&
                        point.land() &&
                        point.baseLandHeight >= originBaseLandHeight - 1 &&
                        point.baseLandHeight <= originBaseLandHeight + 1 &&
                        (point.baseLandHeight > 2 || point.distanceToOcean < 3) &&
                        !explored.get(point.index))
                    {
                        if (lastPoint.baseLandHeight != point.baseLandHeight)
                        {
                            queue.enqueue(point.index);
                        }
                        else
                        {
                            queue.enqueueFirst(point.index);
                        }
                        range.add(point.index);
                        explored.set(point.index);
                    }
                }
            }
        }

        return range;
    }

    private IntSet placeVolcanicArc(Region region, RandomSource random, int originIndex)
    {
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        final IntSet arc = new IntOpenHashSet();

        queue.enqueue(originIndex);
        explored.set(originIndex);
        arc.add(originIndex);

        // So that near-ocean barriers don't start at 0 distance, now they can follow the [0, 1] contour
        final int originDistanceToDeepOcean = Math.max(1, region.atIndex(originIndex).distanceToDeepOcean);
        final int maxSize = 90 + random.nextInt(50);

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            if (arc.size() > maxSize)
            {
                break;
            }

            for (int dx = -2; dx <= 2; dx++)
            {
                for (int dz = -2; dz <= 2; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);

                    // Only explore the contour within [-1, 2] of the origin
                    // Only explore within the subduction zone
                    if (point != null &&
                        point.oceanDepth == 2 &&
                        point.divergence < 0 &&
                        point.distanceToDeepOcean >= originDistanceToDeepOcean - 1 &&
                        point.distanceToDeepOcean <= originDistanceToDeepOcean + 2 &&
                        !explored.get(point.index))
                    {
                        if (lastPoint.distanceToDeepOcean != point.distanceToDeepOcean)
                        {
                            queue.enqueue(point.index);
                        }
                        else
                        {
                            queue.enqueueFirst(point.index);
                        }
                        arc.add(point.index);
                        explored.set(point.index);
                    }
                }
            }
        }

        return arc;
    }

    private IntSet placeBarrier(Region region, RandomSource random, int originIndex)
    {
        final BitSet explored = new BitSet(region.size());
        final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        final IntSet barrier = new IntOpenHashSet();

        queue.enqueue(originIndex);
        explored.set(originIndex);
        barrier.add(originIndex);

        // So that near-ocean barriers don't start at 0 distance, now they can follow the [0, 1] contour
        final int originDistanceToLand = Math.max(1, region.atIndex(originIndex).distanceToLand);
        final int maxSize = 70 + random.nextInt(40);

        while (!queue.isEmpty())
        {
            final int last = queue.dequeueInt();
            final Region.Point lastPoint = region.atIndex(last);
            if (barrier.size() > maxSize)
            {
                break;
            }

            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    final @Nullable Region.Point point = region.atOffset(last, dx, dz);

                    // Only explore the contour within [-1, 1] of the origin
                    if (point != null &&
                        point.oceanDepth == 2 &&
                        point.distanceToLand >= originDistanceToLand - 1 &&
                        point.distanceToLand <= originDistanceToLand + 1 &&
                        !explored.get(point.index))
                    {
                        if (lastPoint.distanceToLand != point.distanceToLand)
                        {
                            queue.enqueue(point.index);
                        }
                        else
                        {
                            queue.enqueueFirst(point.index);
                        }
                        barrier.add(point.index);
                        explored.set(point.index);
                    }
                }
            }
        }

        return barrier;
    }
}
