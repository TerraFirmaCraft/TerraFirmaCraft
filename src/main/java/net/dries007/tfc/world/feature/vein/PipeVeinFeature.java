/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

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
    protected PipeVein createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, PipeVeinConfig config)
    {
        return new PipeVein(defaultPos(context, chunkX, chunkZ, random, config), random, config);
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

        PipeVein(BlockPos pos, RandomSource random, PipeVeinConfig config)
        {
            super(pos);
            this.sign = config.getSign() < random.nextFloat() ? 1 : -1; // if 0: always \/ if 1: always /\
            float angle = random.nextFloat() * (float) Math.PI * 2;
            this.skewX = Mth.cos(angle);
            this.skewZ = Mth.sin(angle);
            this.skew = Helpers.uniform(random, config.getMinSkew(), 1 + config.getMaxSkew());
            this.slant = Helpers.uniform(random, config.getMinSlant(), 1 + config.getMaxSlant());
        }
    }
}
