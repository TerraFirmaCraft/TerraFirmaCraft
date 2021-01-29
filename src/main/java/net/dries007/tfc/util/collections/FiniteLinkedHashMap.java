/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class FiniteLinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
    private final int maxSize;

    public FiniteLinkedHashMap(int maxSize)
    {
        super(maxSize);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {
        return size() > maxSize;
    }
}