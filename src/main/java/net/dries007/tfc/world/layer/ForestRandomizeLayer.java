/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterTransformLayer;

import static net.dries007.tfc.world.layer.TFCLayers.*;

public enum ForestRandomizeLayer implements CenterTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int value)
    {
        if (value == FOREST_NONE)
        {
            final int random = context.random().nextInt(16);
            if (random <= 2)
            {
                return FOREST_SPARSE;
            }
            else if (random == 3)
            {
                return FOREST_NORMAL;
            }
        }
        else if (value == FOREST_SPARSE)
        {
            final int random = context.random().nextInt(7);
            if (random <= 3)
            {
                return FOREST_NORMAL;
            }
        }
        else if (value == FOREST_NORMAL || value == FOREST_OLD)
        {
            final int random = context.random().nextInt(24);
            if (random == 1 && value != FOREST_OLD)
            {
                return FOREST_SPARSE;
            }
            else if (random == 2)
            {
                return FOREST_NONE;
            }
            else if (random <= 6)
            {
                return FOREST_OLD;
            }
        }
        return value;
    }
}
