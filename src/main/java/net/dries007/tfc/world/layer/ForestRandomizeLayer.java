/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.util.RandomSource;

import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterTransformLayer;

public enum ForestRandomizeLayer implements CenterTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int value)
    {
        final ForestType current = ForestType.valueOf(value);
        final RandomSource source = context.random();
        if (current.isNone())
        {
            final int random = source.nextInt(16);
            if (random <= 2)
            {
                return ForestType.SECONDARY_SPARSE.ordinal();
            }
            else if (random == 3)
            {
                return ForestType.getNormalForestType(source);
            }
        }
        else if (current.isSparse())
        {
            final int random = source.nextInt(7);
            if (random <= 3)
            {
                return ForestType.getNormalForestType(source);
            }
        }
        else if (current.isPrimary() || current.isSecondary())
        {
            final int random = source.nextInt(24);
            if (random == 1 && !current.isSecondary())
            {
                return ForestType.SECONDARY_SPARSE.ordinal();
            }
            else if (random == 2)
            {
                return ForestType.SHRUBLAND.ordinal();
            }
            else if (random <= 6)
            {
                return ForestType.getOldGrowthForestType(source);
            }
        }
        return value;
    }
}
