/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.Metaballs3D;

public class ClusterVeinFeature extends VeinFeature<VeinConfig, ClusterVeinFeature.ClusterVein>
{
    public ClusterVeinFeature(Codec<VeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, ClusterVein vein, VeinConfig config)
    {
        return vein.metaballs.inside(x, y, z) ? config.getDensity() : 0;
    }

    @Override
    protected ClusterVein createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, VeinConfig config)
    {
        return new ClusterVein(defaultPos(context, chunkX, chunkZ, random, config), random, config.getSize());
    }

    @Override
    protected BoundingBox getBoundingBox(VeinConfig config, ClusterVein vein)
    {
        return new BoundingBox(-config.getSize(), -config.getSize(), -config.getSize(), config.getSize(), config.getSize(), config.getSize());
    }

    static class ClusterVein extends Vein
    {
        final Metaballs3D metaballs;

        ClusterVein(BlockPos pos, RandomSource random, int size)
        {
            super(pos);
            this.metaballs = Metaballs3D.simple(random, size);
        }
    }
}
