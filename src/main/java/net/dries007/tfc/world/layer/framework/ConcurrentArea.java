/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

import java.util.function.IntFunction;

/**
 * A concurrent wrapper around {@link Area} via the underlying {@link AreaFactory}.
 * Also supports simple mapping of the output to another type.
 */
public class ConcurrentArea<T>
{
    private final ThreadLocal<Area> area;
    private final IntFunction<T> mappingFunction;

    public ConcurrentArea(AreaFactory factory, IntFunction<T> mappingFunction)
    {
        this.area = ThreadLocal.withInitial(factory);
        this.mappingFunction = mappingFunction;
    }

    public T get(int x, int z)
    {
        return mappingFunction.apply(area.get().get(x, z));
    }
}
