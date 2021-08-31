/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.*;

import com.mojang.datafixers.util.Pair;

public interface IWeighted<E> extends Iterable<E>
{
    /**
     * Empty instance, do not use directly. See {@link IWeighted#empty()}
     */
    IWeighted<?> EMPTY = new IWeighted<>()
    {
        @Override
        public void add(double weight, Object element)
        {
            throw new UnsupportedOperationException("Cannot add to singleton EMPTY weighted list!");
        }

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

    @SuppressWarnings("unchecked")
    static <E> IWeighted<E> empty()
    {
        return (IWeighted<E>) EMPTY;
    }

    static <E> IWeighted<E> singleton(E element)
    {
        return new IWeighted<>()
        {
            private final Collection<E> elementSet = Collections.singleton(element);

            @Override
            public void add(double weight, E element)
            {
                throw new UnsupportedOperationException("Cannot add an element to a singleton IWeighted<E>");
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
            public List<Pair<E, Double>> weightedValues()
            {
                return Collections.singletonList(Pair.of(element, 1d));
            }

            @Override
            public boolean isEmpty()
            {
                return false;
            }

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

    /**
     * Adds an element to the list
     *
     * @param weight  The weight of the element, must be positive
     * @param element The element
     */
    void add(double weight, E element);

    /**
     * Gets an element from the weighted list at random
     *
     * @param random A random to use in selection
     * @return An element
     */
    E get(Random random);

    /**
     * @return All possible values for the weighted list
     */
    Collection<E> values();

    /**
     * Serialize the internal representation of the weighted list into a series of weighted pairs.
     * This SHOULD NOT be called often as it may create the data structure on the fly.
     *
     * @return A serializable representation of the weighted list.
     */
    List<Pair<E, Double>> weightedValues();

    /**
     * @return true if the weighted list has no elements.
     */
    boolean isEmpty();
}