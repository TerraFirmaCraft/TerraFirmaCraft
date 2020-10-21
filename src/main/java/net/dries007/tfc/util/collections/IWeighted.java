/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.collections;

import java.util.*;
import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

public interface IWeighted<E> extends Iterable<E>
{
    /**
     * Empty instance, do not use directly. See {@link IWeighted#empty()}
     */
    IWeighted<?> EMPTY = new IWeighted<Object>()
    {
        @Override
        public void add(double weight, Object element) {}

        @Override
        public Object get(Random random)
        {
            return null;
        }

        @Override
        public Collection<Object> values()
        {
            return Collections.emptyList();
        }

        @Override
        public List<Pair<Object, Double>> weightedValues()
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
        public Iterator<Object> iterator()
        {
            return Collections.emptyIterator();
        }

        @Override
        public String toString()
        {
            return "[]";
        }
    };

    static <E> Codec<IWeighted<E>> codec(Codec<List<Pair<E, Double>>> codec)
    {
        return codec.xmap((list -> {
            if (list.isEmpty())
            {
                return IWeighted.empty();
            }
            else if (list.size() == 1)
            {
                return IWeighted.singleton(list.get(0).getFirst());
            }
            else
            {
                return new Weighted<E>(list);
            }
        }), IWeighted::weightedValues);
    }

    @SuppressWarnings("unchecked")
    static <E> IWeighted<E> empty()
    {
        return (IWeighted<E>) EMPTY;
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

            @Override
            public List<Pair<E, Double>> weightedValues()
            {
                return Collections.singletonList(Pair.of(element, 1d));
            }
        };
    }

    void add(double weight, E element);

    E get(Random random);

    Collection<E> values();

    List<Pair<E, Double>> weightedValues();

    boolean isEmpty();
}