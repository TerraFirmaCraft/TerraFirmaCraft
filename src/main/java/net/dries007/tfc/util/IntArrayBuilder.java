package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import net.minecraft.util.IIntArray;
import net.minecraft.util.IntReferenceHolder;

public class IntArrayBuilder implements IIntArray
{
    /**
     * A single {@link IntReferenceHolder} for one tracked value
     */
    public IntReferenceHolder of(IntSupplier getter, IntConsumer setter)
    {
        return new IntReferenceHolder() {
            @Override
            public int get()
            {
                return getter.getAsInt();
            }

            @Override
            public void set(int value)
            {
                setter.accept(value);
            }
        };
    }

    private final List<IntSupplier> getters;
    private final List<IntConsumer> setters;
    private int size;

    public IntArrayBuilder()
    {
        size = 0;
        getters = new ArrayList<>();
        setters = new ArrayList<>();
    }

    public IntArrayBuilder add(IntSupplier getter, IntConsumer setter)
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
