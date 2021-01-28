/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Does what it says on the tin really.
 */
public interface Cache
{
    void reload();

    class Boolean implements Cache
    {
        private final BooleanSupplier supplier;
        private boolean value;

        public Boolean(BooleanSupplier supplier)
        {
            this.supplier = supplier;
        }

        public void reload()
        {
            value = supplier.getAsBoolean();
        }

        public boolean get()
        {
            return value;
        }
    }

    class Int implements Cache
    {
        private final IntSupplier supplier;
        private int value;

        public Int(IntSupplier supplier)
        {
            this.supplier = supplier;
        }

        public void reload()
        {
            value = supplier.getAsInt();
        }

        public int get()
        {
            return value;
        }
    }

    class Double implements Cache
    {
        private final DoubleSupplier supplier;
        private double value;

        public Double(DoubleSupplier supplier)
        {
            this.supplier = supplier;
        }

        public void reload()
        {
            value = supplier.getAsDouble();
        }

        public double get()
        {
            return value;
        }
    }

    class Object<T> implements Cache
    {
        private final Supplier<T> supplier;
        private T value;

        public Object(Supplier<T> supplier)
        {
            this.supplier = supplier;
        }

        public void reload()
        {
            value = supplier.get();
        }

        public T get()
        {
            return value;
        }
    }
}
