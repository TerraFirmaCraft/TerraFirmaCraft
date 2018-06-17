/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.util.OreSpawnData;

public class VeinTypeCluster extends VeinType
{
    private final double verticalModifier;
    private final double horizontalModifier;

    private final Cluster[] spawnPoints;

    public VeinTypeCluster(BlockPos pos, OreSpawnData.OreEntry data, Ore.Grade grade, Random rand)
    {
        super(pos, data, grade);

        this.horizontalModifier = (1.5 - rand.nextDouble()) * data.size.radius;
        this.verticalModifier = (1.5 - rand.nextDouble()) * data.size.radius;

        int clusters = data.type.minClusters;
        if (data.type.maxClusters > clusters)
        {
            clusters += rand.nextInt(data.type.maxClusters - data.type.minClusters);
        }
        spawnPoints = new Cluster[clusters];
        spawnPoints[0] = new Cluster(pos, 0.6 + 0.5 * rand.nextDouble());
        for (int i = 1; i < clusters; i++)
        {
            final BlockPos clusterPos = pos.add(
                1.5 * horizontalModifier * (0.5 - rand.nextDouble()),
                1.5 * verticalModifier * (0.5 - rand.nextDouble()),
                1.5 * horizontalModifier * (0.5 - rand.nextDouble())
            );
            spawnPoints[i] = new Cluster(clusterPos, 0.3 + 0.5 * rand.nextDouble());
        }
    }

    @Override
    public double getChanceToGenerate(BlockPos pos1)
    {
        double shortestRadius = -1;

        for (Cluster c : spawnPoints)
        {
            final double dx = Math.pow(c.pos.getX() - pos1.getX(), 2);
            final double dy = Math.pow(c.pos.getY() - pos1.getY(), 2);
            final double dz = Math.pow(c.pos.getZ() - pos1.getZ(), 2);

            final double radius = (dx + dz) / Math.pow(c.size * horizontalModifier, 2) + dy / Math.pow(c.size * verticalModifier, 2);

            if (shortestRadius == -1 || radius < shortestRadius) shortestRadius = radius;
        }
        return oreSpawnData.density * oreSpawnData.size.densityModifier * (1.0 - shortestRadius);
    }

    private final class Cluster
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
