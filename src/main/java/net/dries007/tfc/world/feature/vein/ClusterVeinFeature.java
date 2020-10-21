package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

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
