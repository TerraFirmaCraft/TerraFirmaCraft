/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
