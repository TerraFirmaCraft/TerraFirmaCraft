/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.SourceLayer;

public class RockLayer implements SourceLayer
{
    private final int totalRocks;

    public RockLayer(int totalRocks)
    {
        this.totalRocks = totalRocks;
    }

    @Override
    public int apply(AreaContext context, int x, int z)
    {
        return context.random().nextInt(totalRocks);
    }
}