/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterMergeLayer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER_MARKER;

public enum MergeRiverLayer implements CenterMergeLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int main, int river)
    {
        return river == RIVER_MARKER && TFCLayerUtil.hasRiver(main) ? TFCLayerUtil.riverFor(main) : main;
    }
}