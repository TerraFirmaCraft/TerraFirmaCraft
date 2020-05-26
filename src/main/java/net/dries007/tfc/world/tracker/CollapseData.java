/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.tracker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public class CollapseData implements INBTSerializable<CompoundNBT>
{
    BlockPos centerPos;
    List<BlockPos> nextPositions;
    double radiusSquared;

    public CollapseData(BlockPos centerPos, List<BlockPos> nextPositions, double radiusSquared)
    {
        this.centerPos = centerPos;
        this.nextPositions = nextPositions;
        this.radiusSquared = radiusSquared;
    }

    public CollapseData(CompoundNBT nbt)
    {
        deserializeNBT(nbt);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("centerPos", centerPos.toLong());
        nbt.putLongArray("nextPositions", nextPositions.stream().mapToLong(BlockPos::toLong).toArray());
        nbt.putDouble("radiusSquared", radiusSquared);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null)
        {
            centerPos = BlockPos.fromLong(nbt.getLong("centerPos"));
            nextPositions = Arrays.stream(nbt.getLongArray("nextPositions")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
            radiusSquared = nbt.getDouble("radiusSquared");
        }
    }
}
