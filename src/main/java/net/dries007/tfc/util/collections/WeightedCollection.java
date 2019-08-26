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
public class WeightedCollection<E>
{
    private final NavigableMap<Double, E> backingMap = new TreeMap<>();
    private double totalWeight = 0;

    public WeightedCollection() {}

    public WeightedCollection(Map<? extends E, Double> values)
    {
        values.forEach((k, v) -> add(v, k));
    }

    public WeightedCollection<E> add(double weight, @Nonnull E result)
    {
        if (weight > 0)
        {
            totalWeight += weight;
            backingMap.put(totalWeight, result);
        }
        return this;
    }

    @Nonnull
    public E getRandomEntry(Random random)
    {
        double value = random.nextDouble() * totalWeight;
        return backingMap.higherEntry(value).getValue();
    }

    public Collection<E> values()
    {
        return backingMap.values();
    }

    public double getTotalWeight()
    {
        return totalWeight;
    }

    public void clear()
    {
        backingMap.clear();
    }
}
