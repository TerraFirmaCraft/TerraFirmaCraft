/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterTransformLayer;

public enum ForestRandomizeSmallLayer implements CenterTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int value)
    {
        final ForestType current = ForestType.valueOf(value);
        if (current.isPrimary() || current.isSecondary())
        {
            final int random = context.random().nextInt((current.isSecondary() ? 40 : 25));
            if (random == 0)
            {
                return ForestType.GRASSLAND.ordinal();
            }
            else if (random == 1)
            {
                return ForestType.SECONDARY_SPARSE.ordinal();
            }
            else if (random == 3)
            {
                return ForestType.getDeadForestType(context.random());
            }
        }
        else if (current.isSparse() || current.isNone())
        {
            final int random = context.random().nextInt(30);
            if (random == 0)
            {
                return current.isSparse() ? ForestType.getNormalForestType(context.random()) : ForestType.getEdgeForestType(context.random());
            }
        }
        if (context.random().nextInt(10) == 0)
        {
            return ForestType.valueOf(value).getAlternate().ordinal();
        }
        return value;
    }
}
