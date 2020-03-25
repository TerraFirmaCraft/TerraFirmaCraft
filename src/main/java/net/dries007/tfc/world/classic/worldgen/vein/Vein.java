/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;

public class Vein
{
    /**
     * Serializes a vein to be saved to chunk data
     *
     * @param vein the vein to be serialized
     * @return a minimal representation of the vein in NBT
     */
    public static NBTTagCompound serialize(Vein vein)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("pos", vein.pos.toLong());
        nbt.setByte("grade", (byte) vein.grade.ordinal());
        if (vein.type != null)
        {
            nbt.setString("type", vein.type.getRegistryName());
        }
        return nbt;
    }

    /**
     * Create a dummy vein from chunk data.
     * Note: this is NOT an exact copy - it should ONLY be used to check equality, as it will not actually generate the vein
     *
     * @param nbt The nbt data for a vein
     * @return a new vein representing the NBT
     */
    public static Vein deserialize(NBTTagCompound nbt)
    {
        BlockPos pos = BlockPos.fromLong(nbt.getLong("pos"));
        Ore.Grade grade = Ore.Grade.valueOf(nbt.getByte("grade"));
        VeinType type = VeinRegistry.INSTANCE.getVein(nbt.getString("type"));
        return new Vein(pos, type, grade);
    }

    protected final BlockPos pos;
    protected final VeinType type;
    protected final Ore.Grade grade;

    Vein(BlockPos pos, VeinType type, Ore.Grade grade)
    {
        this.pos = pos;
        this.type = type;
        this.grade = grade;
    }

    /**
     * Checks if a position is within the horizontal range of a vein spawning
     * The full area is a circle of radius width, centered on the veins position
     *
     * @param x             the x position
     * @param z             the z position
     * @param extraDistance an additional radius to check (veins use 0, indicators use 8)
     */
    public boolean inRange(int x, int z, int extraDistance)
    {
        return pos.distanceSq(x, pos.getY(), z) < (type.getWidth() + extraDistance) * (type.getWidth() + extraDistance);
    }

    /**
     * Gets the state to generate, at a specific position
     */
    public IBlockState getOreState(Rock rock)
    {
        return type.getOreState(rock, grade);
    }

    /**
     * Can the vein spawn in the specified rock type
     */
    public boolean canSpawnIn(Rock rock)
    {
        return type.canSpawnIn(rock);
    }

    /**
     * Get the lowest y position this vein can generate at
     * This is given by the y position minus the height of the vein
     */
    public int getLowestY()
    {
        return Math.max(pos.getY() - type.getHeight(), 1);
    }

    /**
     * Get the highest y position this vein can generate at
     * This is given by the y position plus the height of the vein
     */
    public int getHighestY()
    {
        return Math.min(pos.getY() + type.getHeight(), 255);
    }

    /**
     * Get the chance to generate at a given position
     * Different for different vein shapes
     */
    public double getChanceToGenerate(BlockPos pos)
    {
        return 0;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * Should mostly always returns a VeinType obj (loaded from ore vein config)
     * Unless this vein generated before config was changed, and this vein registry deleted
     */
    @Nullable
    public VeinType getType()
    {
        return type;
    }

    public Ore.Grade getGrade()
    {
        return grade;
    }

    @Override
    public int hashCode()
    {
        return (pos.hashCode() * 3) + grade.ordinal();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Vein)
        {
            Vein other = (Vein) obj;
            return other.pos.equals(this.pos) && other.type == this.type && other.grade == this.grade;
        }
        return false;
    }
}
