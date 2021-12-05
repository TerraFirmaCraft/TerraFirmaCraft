/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterTransformLayer;

import static net.dries007.tfc.world.layer.TFCLayers.INLAND_MARKER;
import static net.dries007.tfc.world.layer.TFCLayers.LAKE_MARKER;

/**
 * Adds instances of lake markers to a layer randomly
 */
public enum AddLakesLayer implements CenterTransformLayer
{
    SMALL(40),
    LARGE(160);

    private final int chance;

    AddLakesLayer(int chance)
    {
        this.chance = chance;
    }

    @Override
    public int apply(AreaContext context, int value)
    {
        if (value == INLAND_MARKER && context.random().nextInt(chance) == 0)
        {
            return LAKE_MARKER;
        }
        return value;
    }
}