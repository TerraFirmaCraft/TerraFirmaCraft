/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.collections;

import java.util.*;
import javax.annotation.Nonnull;

/**
 * Modified from https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
 *
 * @param <E> the type of the collection
 */
public class Weighted<E> implements IWeighted<E>
{
    private final NavigableMap<Double, E> backingMap = new TreeMap<>();
    private double totalWeight = 0;

    public Weighted() {}

    public Weighted(Map<? extends E, Double> values)
    {
        values.forEach((k, v) -> add(v, k));
    }

    @Override
    public void add(double weight, E result)
    {
        if (weight > 0)
        {
            totalWeight += weight;
            backingMap.put(totalWeight, result);
        }
    }

    @Override
    public E get(Random random)
    {
        double value = random.nextDouble() * totalWeight;
        return backingMap.higherEntry(value).getValue();
    }

    @Override
    public Collection<E> values()
    {
        return backingMap.values();
    }

    @Override
    public boolean isEmpty()
    {
        return backingMap.isEmpty();
    }

    @Nonnull
    @Override
    public Iterator<E> iterator()
    {
        return backingMap.values().iterator();
    }
}
