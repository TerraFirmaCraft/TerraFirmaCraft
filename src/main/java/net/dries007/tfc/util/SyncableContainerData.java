/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import net.minecraft.world.inventory.ContainerData;

public class SyncableContainerData implements ContainerData
{
    private final List<IntSupplier> getters;
    private final List<IntConsumer> setters;
    private int size;

    public SyncableContainerData()
    {
        size = 0;
        getters = new ArrayList<>();
        setters = new ArrayList<>();
    }

    public SyncableContainerData add(IntSupplier getter, IntConsumer setter)
    {
        size++;
        getters.add(getter);
        setters.add(setter);
        return this;
    }

    @Override
    public int get(int index)
    {
        return getters.get(index).getAsInt();
    }

    @Override
    public void set(int index, int value)
    {
        setters.get(index).accept(value);
    }

    @Override
    public int getCount()
    {
        return size;
    }
}
