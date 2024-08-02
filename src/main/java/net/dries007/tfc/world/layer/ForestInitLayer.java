/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.SourceLayer;
import net.dries007.tfc.world.noise.Noise2D;

public class ForestInitLayer implements SourceLayer
{
    private final Noise2D forestBaseNoise;

    ForestInitLayer(Noise2D forestBaseNoise)
    {
        this.forestBaseNoise = forestBaseNoise;
    }

    @Override
    public int apply(AreaContext context, int x, int z)
    {
        final float noise = (float) forestBaseNoise.noise(x, z);
        if (noise < 0)
        {
            return ForestType.GRASSLAND.ordinal();
        }
        else
        {
            return ForestType.SECONDARY_DIVERSE.ordinal();
        }
    }
}
