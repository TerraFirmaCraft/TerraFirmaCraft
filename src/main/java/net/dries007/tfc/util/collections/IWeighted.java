package net.dries007.tfc.util.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nonnull;

public interface IWeighted<E> extends Iterable<E>
{
    static <E> IWeighted<E> empty()
    {
        return new IWeighted<E>()
        {
            @Override
            public void add(double weight, E element) {}

            @Override
            public E get(Random random)
            {
                return null;
            }

            @Override
            public Collection<E> values()
            {
                return Collections.emptyList();
            }

            @Override
            public boolean isEmpty()
            {
                return true;
            }

            @Nonnull
            @Override
            public Iterator<E> iterator()
            {
                return Collections.emptyIterator();
            }

            @Override
            public String toString()
            {
                return "[]";
            }
        };
    }

    static <E> IWeighted<E> singleton(E element)
    {
        return new IWeighted<E>()
        {
            private final Collection<E> elementSet = Collections.singleton(element);

            @Override
            public void add(double weight, E element)
            {
                throw new IllegalStateException("Tried to add an element to a singleton IWeighted<E>");
            }

            @Override
            public E get(Random random)
            {
                return element;
            }

            @Override
            public Collection<E> values()
            {
                return elementSet;
            }

            @Override
            public boolean isEmpty()
            {
                return false;
            }

            @Nonnull
            @Override
            public Iterator<E> iterator()
            {
                return elementSet.iterator();
            }

            @Override
            public String toString()
            {
                return "[" + element + "]";
            }
        };
    }

    void add(double weight, E element);

    E get(Random random);

    Collection<E> values();

    boolean isEmpty();
}
