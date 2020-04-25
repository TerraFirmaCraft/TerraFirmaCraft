package net.dries007.tfc.world.vein;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.Metaballs2D;

public class DiscVeinType extends VeinType<DiscVeinType.DiscVein>
{
    private final int height;

    public DiscVeinType(JsonObject json, JsonDeserializationContext context)
    {
        super(json, context);

        height = JSONUtils.getInt(json, "height", 4);
    }

    @Override
    public DiscVein createVein(int chunkXStart, int chunkZStart, Random rand)
    {
        return new DiscVein(this, new BlockPos(chunkXStart + rand.nextInt(16), defaultYPos(height / 2, rand), chunkZStart + rand.nextInt(16)), rand);
    }

    @Override
    public boolean inRange(DiscVein vein, int x, int z)
    {
        return -size <= x && x <= size && -size <= z && z <= size;
    }

    @Override
    public float getChanceToGenerate(DiscVein vein, int x, int y, int z)
    {
        if (Math.abs(y) * 2 <= height)
        {
            return vein.metaballs.noise(x, z) * density;
        }
        return 0;
    }

    static class DiscVein extends Vein<DiscVeinType>
    {
        final INoise2D metaballs;

        DiscVein(DiscVeinType type, BlockPos pos, Random rand)
        {
            super(type, pos);
            metaballs = new Metaballs2D(type.size, rand);
        }
    }
}
