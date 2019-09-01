/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.objectholders;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class TypedItemTable<T, K, V extends IItemProvider & IForgeRegistryEntry<? super V>>
{
    private final Class<V> objectClass;
    private final IForgeRegistry<? super V> registry;
    private final Map<T, Map<K, V>> objects;

    public TypedItemTable(Class<V> objectClass, IForgeRegistry<? super V> registry)
    {
        this.objectClass = objectClass;
        this.registry = registry;
        this.objects = new HashMap<>();
    }

    public V add(T type1, K type2, V block)
    {
        objects.computeIfAbsent(type1, key -> new HashMap<>()).put(type2, block);
        return block;
    }

    public V get(T type1, K type2)
    {
        return objects.get(type1).get(type2);
    }

    public ItemStack get(T type1, K type2, int amount)
    {
        return new ItemStack(objects.get(type1).get(type2), amount);
    }

    @SuppressWarnings("unchecked")
    public void reload()
    {
        for (Map.Entry<T, Map<K, V>> outerEntry : objects.entrySet())
        {
            for (Map.Entry<K, V> entry : outerEntry.getValue().entrySet())
            {
                Object obj = registry.getValue(entry.getValue().getRegistryName());
                if (obj != entry.getValue())
                {
                    if (!objectClass.isInstance(obj))
                    {
                        throw new IllegalStateException("A registry entry was replaced with an invalid subclass! This is due to a mod incorrectly trying to mess with TFC!");
                    }
                    entry.setValue((V) obj);
                }
            }
        }
    }
}
