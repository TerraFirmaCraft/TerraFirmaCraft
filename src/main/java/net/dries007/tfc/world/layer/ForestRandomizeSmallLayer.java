/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterTransformLayer;

import static net.dries007.tfc.world.layer.TFCLayers.*;

public enum ForestRandomizeSmallLayer implements CenterTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int value)
    {
        if (value == FOREST_NORMAL || value == FOREST_OLD)
        {
            final int random = context.random().nextInt((value == FOREST_OLD ? 40 : 25));
            if (random == 0)
            {
                value = FOREST_NONE;
            }
            else if (random == 1)
            {
                value = FOREST_SPARSE;
            }
        }
        else if (value == FOREST_SPARSE || value == FOREST_NONE)
        {
            final int random = context.random().nextInt(30);
            if (random == 0)
            {
                value = value == FOREST_SPARSE ? FOREST_NORMAL : FOREST_EDGE;
            }
        }
        return switch (value)
        {
            case 0 -> ForestType.GRASSLAND.ordinal();
            case 1 -> ForestType.getSparseForestType(context.random());
            case 2 -> ForestType.getEdgeForestType(context.random());
            case 3 -> ForestType.getNormalForestType(context.random());
            default -> ForestType.getOldGrowthForestType(context.random());
        };
    }
}
