package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import com.mojang.serialization.Codec;

public class PipeVeinFeature extends VeinFeature<PipeVeinConfig, Vein>
{
    public PipeVeinFeature(Codec<PipeVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected boolean inRange(int x, int z, PipeVeinConfig config)
    {
        return (x * x) + (z * z) < config.getRadius() * config.getRadius();
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, Vein vein, PipeVeinConfig config)
    {
        return Math.abs(y) < config.getSize() ? config.getDensity() : 0;
    }

    @Override
    protected Vein createVein(int chunkX, int chunkZ, Random random, PipeVeinConfig config)
    {
        return new Vein(defaultPos(chunkX, chunkZ, random, config));
    }
}
