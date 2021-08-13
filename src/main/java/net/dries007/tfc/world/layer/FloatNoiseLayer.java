/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.SourceLayer;
import net.dries007.tfc.world.noise.Noise2D;

public class FloatNoiseLayer implements SourceLayer
{
    private final Noise2D noise;

    public FloatNoiseLayer(Noise2D noise)
    {
        this.noise = noise;
    }

    @Override
    public int apply(AreaContext context, int x, int z)
    {
        return Float.floatToRawIntBits(noise.noise(x, z));
    }
}
