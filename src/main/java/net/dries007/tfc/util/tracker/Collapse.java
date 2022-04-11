/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

public class Collapse
{
    BlockPos centerPos;
    List<BlockPos> nextPositions;
    double radiusSquared;

    public Collapse(BlockPos centerPos, List<BlockPos> nextPositions, double radiusSquared)
    {
        this.centerPos = centerPos;
        this.nextPositions = nextPositions;
        this.radiusSquared = radiusSquared;
    }

    public Collapse(CompoundTag nbt)
    {
        centerPos = BlockPos.of(nbt.getLong("centerPos"));
        nextPositions = Arrays.stream(nbt.getLongArray("nextPositions")).mapToObj(BlockPos::of).collect(Collectors.toList());
        radiusSquared = nbt.getDouble("radiusSquared");
    }

    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("centerPos", centerPos.asLong());
        nbt.putLongArray("nextPositions", nextPositions.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putDouble("radiusSquared", radiusSquared);
        return nbt;
    }
}