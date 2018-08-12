/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.chunkdata;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.util.OreSpawnData;

public class ChunkDataOreSpawned
{
    public final Ore ore;
    public final OreSpawnData.SpawnSize size;
    public final Ore.Grade grade;
    public final BlockPos pos;

    public ChunkDataOreSpawned(@Nonnull Ore ore, OreSpawnData.SpawnSize size, Ore.Grade grade, BlockPos pos)
    {
        this.ore = ore;
        this.size = size;
        this.grade = grade;
        this.pos = pos;
    }

    public ChunkDataOreSpawned(NBTTagCompound tag)
    {
        ore = Ore.get(tag.getString("ore"));
        grade = Ore.Grade.valueOf(tag.getString("grade").toUpperCase());
        size = OreSpawnData.SpawnSize.valueOf(tag.getString("size").toUpperCase());
        pos = NBTUtil.getPosFromTag(tag.getCompoundTag("pos"));
    }

    public NBTTagCompound serialize()
    {
        NBTTagCompound root = new NBTTagCompound();
        root.setString("ore", ore.name());
        root.setString("grade", grade.name());
        root.setString("size", size.name());
        root.setTag("pos", NBTUtil.createPosTag(pos));
        return root;
    }

    @Override
    public String toString()
    {
        return "ChunkDataOreSpawned{" +
            "ore=" + ore +
            ", size=" + size +
            ", grade=" + grade +
            ", pos=" + pos +
            '}';
    }
}
