package net.dries007.tfc.util;

import net.minecraft.world.inventory.ContainerData;

/**
 * {@linkplain net.minecraft.world.inventory.SimpleContainerData} but with public access to the array
 */
public class ArrayContainerData implements ContainerData
{
    private final int[] ints;

    public ArrayContainerData(int size)
    {
        this.ints = new int[size];
    }

    @Override
    public int get(int index)
    {
        return this.ints[index];
    }

    @Override
    public void set(int index, int value)
    {
        this.ints[index] = value;
    }

    @Override
    public int getCount()
    {
        return this.ints.length;
    }

    public int[] getArray()
    {
        return ints;
    }
}
