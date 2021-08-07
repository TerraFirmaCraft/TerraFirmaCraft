/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * An {@link MergeLayer} which merges the two areas by querying their target value.
 */
public interface CenterMergeLayer extends MergeLayer
{
    @Override
    default int apply(AreaContext context, Area first, Area second, int x, int z)
    {
        return apply(context, first.get(x, z), second.get(x, z));
    }

    int apply(AreaContext context, int first, int second);
}
