/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterMergeLayer;

import static net.dries007.tfc.world.layer.TFCLayers.LAKE_MARKER;

/**
 * Mixes lakes into the standard biome layer
 */
public enum MergeLakeLayer implements CenterMergeLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int main, int lake)
    {
        return lake == LAKE_MARKER && TFCLayers.hasLake(main) ? TFCLayers.lakeFor(main) : main;
    }
}
