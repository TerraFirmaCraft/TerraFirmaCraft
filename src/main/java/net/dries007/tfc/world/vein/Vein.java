package net.dries007.tfc.world.vein;

import net.minecraft.util.math.BlockPos;

public class Vein<T extends VeinType<?>>
{
    private final T type;
    private final BlockPos pos;

    public Vein(T type, BlockPos pos)
    {
        this.type = type;
        this.pos = pos;
    }

    public T getType()
    {
        return type;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * Checks if the vein is in range of a point.
     * This should typically call {@code getType().inRange()}
     *
     * @param x absolute x position
     * @param z absolute z position
     * @return if the vein can generate at any y position in that column
     */
    public boolean inRange(int x, int z)
    {
        return getTypeRaw().inRange(this, pos.getX() - x, pos.getZ() - z);
    }

    /**
     * Gets the chance to generate at a position
     * This should typically call {@code getType().getChanceToGenerate()}
     *
     * @param pos a position to generate at
     * @return a chance, with <= 0 meaning no chance, >= 1 indicating 100% chance
     */
    public double getChanceToGenerate(BlockPos pos)
    {
        return getTypeRaw().getChanceToGenerate(this, pos.getX() - this.pos.getX(), pos.getY() - this.pos.getY(), pos.getZ() - this.pos.getZ());
    }

    @SuppressWarnings("unchecked")
    private <V extends Vein<T>> VeinType<V> getTypeRaw()
    {
        return ((VeinType<V>) type);
    }
}
