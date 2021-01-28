/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for a list which needs to be very careful about adding while iterating
 * Any new elements added will sit in the buffer list, and have to be manually added to the backing list by calling {@link BufferedList#flush()}
 */
public class BufferedList<E> extends AbstractList<E>
{
    private final List<E> buffer, values;

    public BufferedList()
    {
        this.values = new ArrayList<>();
        this.buffer = new ArrayList<>();
    }

    public void flush()
    {
        values.addAll(buffer);
        buffer.clear();
    }

    @Override
    public boolean add(E e)
    {
        return buffer.add(e);
    }

    @Override
    public E get(int index)
    {
        return values.get(index);
    }

    @Override
    public E remove(int index)
    {
        return values.remove(index);
    }

    @Override
    public void clear()
    {
        values.clear();
        buffer.clear();
    }

    @Override
    public int size()
    {
        return values.size();
    }
}
