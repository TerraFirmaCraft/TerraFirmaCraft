/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

public enum ForestEdgeLayer implements AdjacentTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int north, int east, int south, int west, int center)
    {
        if (isFullForest(center))
        {
            if (!isFullForest(north) || !isFullForest(east) || !isFullForest(south) || !isFullForest(west))
            {
                return ForestType.getEdgeForestType(context.random());
            }
        }
        return center;
    }

    private boolean isFullForest(int value)
    {
        final ForestType type = ForestType.valueOf(value);
        return type.isPrimary() || type.isSecondary();
    }
}
