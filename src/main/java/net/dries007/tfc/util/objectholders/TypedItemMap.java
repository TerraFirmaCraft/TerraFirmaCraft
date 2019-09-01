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

public class TypedItemMap<T, V extends IItemProvider & IForgeRegistryEntry<? super V>>
{
    private final Class<V> objectClass;
    private final IForgeRegistry<? super V> registry;
    private final Map<T, V> objects;

    public TypedItemMap(Class<V> objectClass, IForgeRegistry<? super V> registry)
    {
        this.objectClass = objectClass;
        this.registry = registry;
        this.objects = new HashMap<>();
    }

    public V add(T type, V block)
    {
        objects.put(type, block);
        return block;
    }

    @SuppressWarnings("unchecked")
    public void reload()
    {
        for (Map.Entry<T, V> entry : objects.entrySet())
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

    public V get(T type)
    {
        return objects.get(type);
    }

    public ItemStack get(T type, int amount)
    {
        return new ItemStack(objects.get(type), amount);
    }
}
