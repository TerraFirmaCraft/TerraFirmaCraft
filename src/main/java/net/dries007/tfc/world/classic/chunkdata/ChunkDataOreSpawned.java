/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.chunkdata;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.objects.OreEnum;
import net.dries007.tfc.util.OreSpawnData;

public class ChunkDataOreSpawned
{
    @Nullable
    public final OreEnum ore;
    @Nullable
    public final IBlockState state;
    public final OreSpawnData.SpawnSize size;
    public final OreEnum.Grade grade;
    public final BlockPos pos;
    public final int count;

    public ChunkDataOreSpawned(@Nullable OreEnum ore, @Nullable IBlockState state, OreSpawnData.SpawnSize size, OreEnum.Grade grade, BlockPos pos, int count)
    {
        this.ore = ore;
        this.state = state;
        this.size = size;
        this.grade = grade;
        this.pos = pos;
        this.count = count;
        if (ore == null && state == null) throw new IllegalArgumentException("Both ore and state can't be null.");
    }

    public ChunkDataOreSpawned(NBTTagCompound tag)
    {
        ore = tag.hasKey("ore") ? OreEnum.valueOf(tag.getString("ore").toUpperCase()) : null;
        state = tag.hasKey("state") ? NBTUtil.readBlockState(tag.getCompoundTag("state")) : null;
        grade = OreEnum.Grade.valueOf(tag.getString("grade").toUpperCase());
        size = OreSpawnData.SpawnSize.valueOf(tag.getString("size").toUpperCase());
        pos = NBTUtil.getPosFromTag(tag.getCompoundTag("pos"));
        count = tag.getInteger("count");
    }

    public NBTTagCompound serialize()
    {
        NBTTagCompound root = new NBTTagCompound();
        if (ore != null) root.setString("ore", ore.name());
        if (state != null) root.setTag("state", NBTUtil.writeBlockState(new NBTTagCompound(), state));
        root.setString("grade", grade.name());
        root.setString("size", size.name());
        root.setTag("pos", NBTUtil.createPosTag(pos));
        root.setInteger("count", count);
        return root;
    }

    @Override
    public String toString()
    {
        return "ChunkDataOreSpawned{" +
            "ore=" + ore +
            ", state=" + state +
            ", size=" + size +
            ", grade=" + grade +
            ", pos=" + pos +
            ", count=" + count +
            '}';
    }
}
