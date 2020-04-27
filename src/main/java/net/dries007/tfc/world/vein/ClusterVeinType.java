package net.dries007.tfc.world.vein;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.world.noise.INoise3D;
import net.dries007.tfc.world.noise.Metaballs3D;

/**
 * 3D Implementation of <a href="https://en.wikipedia.org/wiki/Metaballs">Metaballs</a>
 */
public class ClusterVeinType extends VeinType<ClusterVeinType.ClusterVein>
{
    public ClusterVeinType(JsonObject json, JsonDeserializationContext context)
    {
        super(json, context);
    }

    @Override
    public ClusterVein createVein(int chunkXStart, int chunkZStart, Random rand)
    {
        return new ClusterVein(this, new BlockPos(chunkXStart + rand.nextInt(16), defaultYPos(size, rand), chunkZStart + rand.nextInt(16)), rand);
    }

    @Override
    public boolean inRange(ClusterVein vein, int x, int z)
    {
        return -size <= x && x <= size && -size <= z && z <= size;
    }

    @Override
    public float getChanceToGenerate(ClusterVein vein, int x, int y, int z)
    {
        return vein.metaballs.noise(x, y, z);
    }

    static class ClusterVein extends Vein<ClusterVeinType>
    {
        final INoise3D metaballs;

        ClusterVein(ClusterVeinType type, BlockPos pos, Random random)
        {
            super(type, pos);
            this.metaballs = new Metaballs3D(type.size, random);
        }
    }
}
