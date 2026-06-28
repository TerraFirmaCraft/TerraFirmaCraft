/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Noise3D;
import net.dries007.tfc.world.noise.OpenSimplex3D;

public class PipeVeinFeature extends VeinFeature<PipeVeinConfig, PipeVeinFeature.Vein>
{
    public static final Noise3D radiusEdgeNoise = new OpenSimplex3D(15189385L).octaves(2).scaled(0, -9);

    public PipeVeinFeature(Codec<PipeVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, Vein vein, PipeVeinConfig config)
    {
        final double edgeNoise = radiusEdgeNoise.noise(0.25 * x, 0.05 * y, 0.25 * z);

        final double yScaled = (double) y / config.height();
        x += vein.skew * vein.skewX * yScaled;
        z += vein.skew * vein.skewZ * yScaled;

        final double yFactor = (double) vein.sign * yScaled + 0.5D;

        final int absY = Math.abs(y);
        if (absY < config.height())
        {
            double trueRadius = config.radius() * (1 - yFactor) + (config.radius() - vein.slant) * yFactor + edgeNoise;
            final int taperStart = (config.height() - 3 * config.radius());
            if (absY > taperStart)
            {
                trueRadius = Mth.map(absY, taperStart, config.height(), trueRadius, -config.radius());
            }

            if ((x * x) + (z * z) < trueRadius * trueRadius)
            {
                return config.config().density();
            }
        }

        return 0;
    }

    @Override
    protected Vein createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, PipeVeinConfig config)
    {
        final float angle = random.nextFloat() * (float) Math.PI * 2;
        return new Vein(
            defaultPos(chunkX, chunkZ, random, config),
            config.sign() < random.nextFloat() ? 1 : -1,
            Mth.cos(angle), Mth.sin(angle),
            Helpers.uniform(random, config.minSkew(), 1 + config.maxSkew()),
            Helpers.uniform(random, config.minSlant(), 1 + config.maxSlant()));
    }

    @Override
    protected BoundingBox getBoundingBox(PipeVeinConfig config, Vein vein)
    {
        final int radius = config.radius();
        final int skew = vein.skew;
        return new BoundingBox(-radius - skew, -config.height(), -radius - skew, radius + skew, config.height(), radius + skew);
    }

    record Vein(BlockPos pos, int sign, float skewX, float skewZ, int skew, int slant) implements IVein {}
}
