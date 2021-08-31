/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.*;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;

/**
 * Modified from https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
 *
 * @param <E> the type of the collection
 */
public class Weighted<E> implements IWeighted<E>
{
    private final NavigableMap<Double, E> backingMap = new TreeMap<>();
    private double totalWeight = 0;
    private List<Pair<E, Double>> weightedValues;

    public Weighted() {}

    public Weighted(List<Pair<E, Double>> parallelWeightedList)
    {
        parallelWeightedList.forEach(pair -> add(pair.getSecond(), pair.getFirst()));
    }

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
    public List<Pair<E, Double>> weightedValues()
    {
        if (weightedValues == null)
        {
            weightedValues = backingMap.entrySet().stream().map(e -> Pair.of(e.getValue(), e.getKey())).collect(Collectors.toList());
        }
        return weightedValues;
    }

    @Override
    public boolean isEmpty()
    {
        return backingMap.isEmpty();
    }

    @Override
    public Iterator<E> iterator()
    {
        return backingMap.values().iterator();
    }
}