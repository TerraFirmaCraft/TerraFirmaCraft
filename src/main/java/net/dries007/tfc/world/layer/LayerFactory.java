/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.function.IntFunction;

import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;


public class LayerFactory<T>
{
    /**
     * Uses a thread local area, as the underlying area is not synchronized.
     * This is an optimization adapted from Lithium, implementing a much better cache for the LazyArea underneath
     */
    private final ThreadLocal<Area> area;
    private final IntFunction<T> mappingFunction;

    public LayerFactory(AreaFactory factory, IntFunction<T> mappingFunction)
    {
        this.area = ThreadLocal.withInitial(factory);
        this.mappingFunction = mappingFunction;
    }

    public T get(int x, int z)
    {
        return mappingFunction.apply(area.get().get(x, z));
    }
}
