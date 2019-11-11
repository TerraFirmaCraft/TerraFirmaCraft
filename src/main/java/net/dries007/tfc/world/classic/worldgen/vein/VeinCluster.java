/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.types.Ore;

public class VeinCluster extends Vein
{
    private final Cluster[] spawnPoints;

    public VeinCluster(BlockPos pos, VeinType veinType, Ore.Grade grade, Random rand)
    {
        super(pos, veinType, grade);

        // Individual vein width is 60% - 100% of type width (it must fit exactly inside the circle described by width)
        double maxWidth = (0.6 + rand.nextDouble() * 0.4) * veinType.getWidth();
        double maxHeight = (0.6 + rand.nextDouble() * 0.4) * veinType.getHeight();

        int clusters = 3 + rand.nextInt(5);
        double maxClusterSize = 0.6 * maxWidth;
        spawnPoints = new Cluster[clusters];
        spawnPoints[0] = new Cluster(pos, maxClusterSize * (0.6 + 0.4 * rand.nextDouble()));
        for (int i = 1; i < clusters; i++)
        {
            final BlockPos clusterPos = pos.add(
                maxWidth * 0.4 * rand.nextDouble(),
                maxHeight * 0.4 * rand.nextDouble(),
                maxWidth * 0.4 * rand.nextDouble()
            );
            spawnPoints[i] = new Cluster(clusterPos, maxClusterSize * (0.4 + 0.6 * rand.nextDouble()));
        }
    }

    @Override
    public double getChanceToGenerate(BlockPos pos)
    {
        double shortestRadius = -1;
        for (Cluster c : spawnPoints)
        {
            double dx = (c.pos.getX() - pos.getX()) * (c.pos.getX() - pos.getX());
            double dy = (c.pos.getY() - pos.getY()) * (c.pos.getY() - pos.getY());
            double dz = (c.pos.getZ() - pos.getZ()) * (c.pos.getZ() - pos.getZ());
            double radius = (dx * dx) + (dy * dy) + (dz * dz) / (c.size * c.size);

            if (shortestRadius == -1 || radius < shortestRadius)
            {
                shortestRadius = radius;
            }
        }
        return type.getDensity() * (1 - shortestRadius);
    }

    private static final class Cluster
    {
        final BlockPos pos;
        final double size;

        Cluster(BlockPos pos, double size)
        {
            this.pos = pos;
            this.size = size;
        }
    }
}
