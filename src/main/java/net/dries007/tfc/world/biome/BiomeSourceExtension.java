/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.settings.Settings;

public interface BiomeSourceExtension
{
    /**
     * Specialized variant of {@link BiomeSource#getNoiseBiome(int, int, int, Climate.Sampler)}.
     */
    default Holder<Biome> getBiome(int quartX, int quartZ)
    {
        return getBiomeFromExtension(getBiomeExtension(quartX, quartZ));
    }

    default BiomeExtension getBiomeExtension(int quartX, int quartZ)
    {
        final BiomeExtension biome = getBiomeExtensionNoRiver(quartX, quartZ);
        if (biome.hasRivers())
        {
            final RegionPartition.Point partitionPoint = getPartition(QuartPos.toBlock(quartX), QuartPos.toBlock(quartZ));
            final double exactGridX = Units.quartToGridExact(quartX);
            final double exactGridZ = Units.quartToGridExact(quartZ);

            for (RiverEdge edge : partitionPoint.rivers())
            {
                if (edge.fractal().intersect(exactGridX, exactGridZ, 0.08f))
                {
                    return TFCBiomes.RIVER;
                }
            }
        }
        return biome;
    }

    BiomeExtension getBiomeExtensionNoRiver(int quartX, int quartZ);

    /**
     * Looks up a biome by extension in the biome registry.
     */
    Holder<Biome> getBiomeFromExtension(BiomeExtension extension);

    RegionPartition.Point getPartition(int blockX, int blockZ);

    /**
     * Optimized version of {@link BiomeSource#findBiomeHorizontal(int, int, int, int, Predicate, RandomSource, Climate.Sampler)} for finding spawn biomes.
     * Avoids querying rivers, directly queries biome extensions, and uses the intervals specified in the {@code settings}.
     */
    default BlockPos findSpawnBiome(Settings settings, RandomSource random)
    {
        final int step = Math.max(1, settings.spawnDistance() / 256);
        final int centerX = QuartPos.fromBlock(settings.spawnCenterX());
        final int centerZ = QuartPos.fromBlock(settings.spawnCenterZ());
        final int maxRadius = QuartPos.fromBlock(settings.spawnDistance());

        BlockPos found = null;
        int count = 0;

        for (int radius = maxRadius; radius <= maxRadius; radius += step)
        {
            for (int dx = -radius; dx <= radius; dx += step)
            {
                for (int dz = -radius; dz <= radius; dz += step)
                {
                    final int quartX = centerX + dz;
                    final int quartZ = centerZ + dx;
                    final BiomeExtension biome = getBiomeExtensionNoRiver(quartX, quartZ);
                    if (biome.isSpawnable())
                    {
                        if (found == null || random.nextInt(count + 1) == 0)
                        {
                            found = new BlockPos(QuartPos.toBlock(quartX), 0, QuartPos.toBlock(quartZ));
                        }
                        count++;
                    }
                }
            }
        }
        if (found == null)
        {
            TerraFirmaCraft.LOGGER.warn("Unable to find spawn biome!");
            return new BlockPos(settings.spawnCenterX(), 0, settings.spawnCenterZ());
        }
        return found;
    }

    default void initRandomState(RegionGenerator regionGenerator, ConcurrentArea<BiomeExtension> biomeLayer) {}

    /**
     * @return itself, or the underlying biome provider / source
     */
    default BiomeSource self()
    {
        return (BiomeSource) this;
    }

    default BiomeSourceExtension copy()
    {
        return this;
    }
}
