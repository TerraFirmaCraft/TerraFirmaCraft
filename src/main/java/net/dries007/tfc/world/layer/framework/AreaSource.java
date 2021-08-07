/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * The primary result of invoking layers apply() functions.
 * Usually wrapped in a Layer interface which allows randomness via a bound {@link AreaContext}.
 */
public interface AreaSource
{
    int apply(int x, int z);
}
