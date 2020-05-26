/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.config;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.noise.SinNoise;

public enum LayerType
{
    SIN_X((seed, scale) -> new SinNoise(1, 0, 1f / scale, 0).extendY().add(new SimplexNoise2D(seed).octaves(2).spread(12f / scale).scaled(-0.2f, 0.2f))),
    SIN_Z((seed, scale) -> new SinNoise(1, 0, 1f / scale, 0).extendX().add(new SimplexNoise2D(seed).octaves(2).spread(12f / scale).scaled(-0.2f, 0.2f))),
    NOISE((seed, scale) -> new SimplexNoise2D(seed).octaves(4).spread(12f / scale));

    private final ILayerTypeFactory factory;

    LayerType(ILayerTypeFactory factory)
    {
        this.factory = factory;
    }

    public INoise2D create(long seed, int scale)
    {
        return factory.apply(seed, scale);
    }

    private interface ILayerTypeFactory
    {
        INoise2D apply(long seed, int scale);
    }
}
