/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.INoise3D;
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
        return vein.metaballs.noise(x, y, z) * config.getDensity();
    }

    @Override
    protected MutableBoundingBox getBoundingBox(VeinConfig config, ClusterVein vein)
    {
        return new MutableBoundingBox(-config.getSize(), -config.getSize(), -config.getSize(), config.getSize(), config.getSize(), config.getSize());
    }

    @Override
    protected ClusterVein createVein(int chunkX, int chunkZ, Random random, VeinConfig config)
    {
        return new ClusterVein(defaultPos(chunkX, chunkZ, random, config), random, config.getSize());
    }

    static class ClusterVein extends Vein
    {
        final INoise3D metaballs;

        ClusterVein(BlockPos pos, Random random, int size)
        {
            super(pos);
            this.metaballs = new Metaballs3D(size, random);
        }
    }
}
