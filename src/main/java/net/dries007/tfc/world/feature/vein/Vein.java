package net.dries007.tfc.world.feature.vein;

import net.minecraft.util.math.BlockPos;

public class Vein
{
    private final BlockPos pos;

    public Vein(BlockPos pos)
    {
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
