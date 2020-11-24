package net.dries007.tfc.util.tracker;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class TickEntry
{
    private final BlockPos pos;
    private int ticks;

    public TickEntry(CompoundNBT nbt)
    {
        this(BlockPos.of(nbt.getLong("pos")), nbt.getInt("ticks"));
    }

    public TickEntry(BlockPos pos, int ticks)
    {
        this.pos = pos;
        this.ticks = ticks;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public boolean tick()
    {
        this.ticks--;
        return this.ticks == 0;
    }

    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("pos", pos.asLong());
        nbt.putInt("ticks", ticks);
        return nbt;
    }
}
