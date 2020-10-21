package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.INoise2D;
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
        if (Math.abs(y) * 2 <= config.getHeight())
        {
            return vein.metaballs.noise(x, z) * config.getDensity();
        }
        return 0;
    }

    @Override
    protected DiscVein createVein(int chunkX, int chunkZ, Random random, DiscVeinConfig config)
    {
        return new DiscVein(defaultPos(chunkX, chunkZ, random, config), random, config.getSize());
    }

    static class DiscVein extends Vein
    {
        final INoise2D metaballs;

        DiscVein(BlockPos pos, Random rand, int size)
        {
            super(pos);
            metaballs = new Metaballs2D(size, rand);
        }
    }
}
