/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

public class TickEntry
{
    private final BlockPos pos;
    private int ticks;

    public TickEntry(CompoundTag nbt)
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

    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("pos", pos.asLong());
        nbt.putInt("ticks", ticks);
        return nbt;
    }
}
