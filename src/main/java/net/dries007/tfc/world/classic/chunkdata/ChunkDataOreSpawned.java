package net.dries007.tfc.world.classic.chunkdata;

import net.dries007.tfc.objects.blocks.BlockOreTFC;
import net.dries007.tfc.util.OreSpawnData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class ChunkDataOreSpawned
{
    public final BlockOreTFC.Ore ore;
    public final OreSpawnData.SpawnSize size;
    public final BlockOreTFC.Grade grade;
    public final BlockPos pos;
    public final int count;

    public ChunkDataOreSpawned(BlockOreTFC.Ore ore, OreSpawnData.SpawnSize size, BlockOreTFC.Grade grade, BlockPos pos, int count)
    {
        this.ore = ore;
        this.size = size;
        this.grade = grade;
        this.pos = pos;
        this.count = count;
    }

    public ChunkDataOreSpawned(NBTTagCompound tag)
    {
        ore = BlockOreTFC.Ore.valueOf(tag.getString("ore").toUpperCase());
        grade = BlockOreTFC.Grade.valueOf(tag.getString("grade").toUpperCase());
        size = OreSpawnData.SpawnSize.valueOf(tag.getString("size").toUpperCase());
        pos = NBTUtil.getPosFromTag(tag.getCompoundTag("pos"));
        count = tag.getInteger("count");
    }

    public NBTTagCompound serialize()
    {
        NBTTagCompound root = new NBTTagCompound();
        root.setString("ore", ore.name());
        root.setString("grade", grade.name());
        root.setString("size", size.name());
        root.setTag("pos", NBTUtil.createPosTag(pos));
        root.setInteger("count", count);
        return root;
    }

    @Override
    public String toString()
    {
        return "OreSpawned{" +
                "ore=" + ore +
                ", grade=" + grade +
                ", pos=" + pos +
                ", count=" + count +
                '}';
    }
}
