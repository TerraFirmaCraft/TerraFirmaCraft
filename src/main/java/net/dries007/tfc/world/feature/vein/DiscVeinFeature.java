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
import net.dries007.tfc.world.noise.Metaballs2D;

public class DiscVeinFeature extends VeinFeature<DiscVeinConfig, DiscVeinFeature.DiscVein>
{
    public DiscVeinFeature(Codec<DiscVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, DiscVein vein, DiscVeinConfig config)
    {
        if (Math.abs(y) <= config.getHeight() && vein.metaballs.inside(x, z))
        {
            return config.getDensity();
        }
        return 0;
    }

    @Override
    protected DiscVein createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, DiscVeinConfig config)
    {
        return new DiscVein(defaultPos(context, chunkX, chunkZ, random, config), random, config.getSize());
    }

    @Override
    protected BoundingBox getBoundingBox(DiscVeinConfig config, DiscVein vein)
    {
        return new BoundingBox(-config.getSize(), -config.getHeight(), -config.getSize(), config.getSize(), config.getHeight(), config.getSize());
    }

    static class DiscVein extends Vein
    {
        final Metaballs2D metaballs;
        final int width;

        DiscVein(BlockPos pos, RandomSource random, int size)
        {
            super(pos);
            metaballs = Metaballs2D.simple(random, size);
            width = size;
        }
    }
}
