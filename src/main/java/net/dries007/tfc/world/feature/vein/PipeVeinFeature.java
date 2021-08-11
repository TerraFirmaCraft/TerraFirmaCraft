/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.mojang.serialization.Codec;

public class PipeVeinFeature extends VeinFeature<PipeVeinConfig, PipeVeinFeature.PipeVein>
{
    public PipeVeinFeature(Codec<PipeVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, PipeVein vein, PipeVeinConfig config)
    {
        final double yScaled = (double) y / config.getSize();
        x += vein.skew * vein.skewX * yScaled;
        z += vein.skew * vein.skewZ * yScaled;

        final double yFactor = (double) vein.sign * yScaled + 0.5D;
        final double trueRadius = config.getRadius() * (1 - yFactor) + (config.getRadius() - vein.slant) * yFactor;
        if (Math.abs(y) < config.getSize() && (x * x) + (z * z) < trueRadius * trueRadius)
        {
            return config.getDensity();
        }
        return 0;
    }

    @Override
    protected PipeVein createVein(int chunkX, int chunkZ, Random random, PipeVeinConfig config)
    {
        return new PipeVein(defaultPos(chunkX, chunkZ, random, config), random, config);
    }

    @Override
    protected BoundingBox getBoundingBox(PipeVeinConfig config, PipeVein vein)
    {
        int radius = config.getRadius();
        int skew = vein.skew;
        return new BoundingBox(-radius - skew, -config.getSize(), -radius - skew, radius + skew, config.getSize(), radius + skew);
    }

    static class PipeVein extends Vein
    {
        final int sign;
        final float skewX;
        final float skewZ;
        final int skew;
        final int slant;

        PipeVein(BlockPos pos, Random random, PipeVeinConfig config)
        {
            super(pos);
            this.sign = config.getSign() < random.nextFloat() ? 1 : -1; // if 0: always \/ if 1: always /\
            float angle = random.nextFloat() * (float) Math.PI * 2;
            this.skewX = Mth.cos(angle);
            this.skewZ = Mth.sin(angle);
            this.skew = Mth.nextInt(random, config.getMinSkew(), config.getMaxSkew());
            this.slant = Mth.nextInt(random, config.getMinSlant(), config.getMaxSlant());
        }
    }
}
