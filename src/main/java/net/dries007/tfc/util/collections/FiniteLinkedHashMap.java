/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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
