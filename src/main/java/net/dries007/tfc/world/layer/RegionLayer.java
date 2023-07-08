/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TypedSourceLayer;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;

public record RegionLayer(RegionGenerator generator) implements TypedSourceLayer<Region.Point>
{
    @Override
    public Region.Point apply(AreaContext context, int x, int z)
    {
        return generator.getOrCreateRegionPoint(x, z);
    }
}
