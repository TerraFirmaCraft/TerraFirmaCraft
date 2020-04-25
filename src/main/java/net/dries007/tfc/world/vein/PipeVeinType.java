package net.dries007.tfc.world.vein;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

public class PipeVeinType extends VeinType<PipeVeinType.PipeVein>
{
    private final int radius;

    public PipeVeinType(JsonObject json, JsonDeserializationContext context)
    {
        super(json, context);

        radius = JSONUtils.getInt(json, "radius", 3);
    }

    @Override
    public int getChunkRadius()
    {
        return 1 + (radius >> 4);
    }

    @Override
    public PipeVein createVein(int chunkXStart, int chunkZStart, Random rand)
    {
        return new PipeVein(this, new BlockPos(chunkXStart + rand.nextInt(16), defaultYPos(size, rand), chunkZStart + rand.nextInt(16)));
    }

    @Override
    public boolean inRange(PipeVein vein, int x, int z)
    {
        return (x * x) + (z * z) < radius * radius;
    }

    @Override
    public float getChanceToGenerate(PipeVein vein, int x, int y, int z)
    {
        return Math.abs(y) < size ? density : 0;
    }

    static class PipeVein extends Vein<PipeVeinType>
    {
        PipeVein(PipeVeinType type, BlockPos pos)
        {
            super(type, pos);
        }
    }
}
