/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.config;

import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.noise.SinNoise;

/**
 * The noise layer used by temperature and rainfall layer config options
 * - Periodic is regular sin
 * - Gradient is sin cutoff at the peaks
 * - Noise is simplex noise
 */
public enum NoiseLayerType
{
    PERIODIC_X((seed, scale) -> new SinNoise(1, 0, 1f / scale, 0)
        .extendY()
        .add(new SimplexNoise2D(seed).octaves(2).spread(12f / scale).scaled(-0.2f, 0.2f))
    ),
    PERIODIC_Z((seed, scale) -> new SinNoise(1, 0, 1f / scale, 0)
        .extendX()
        .add(new SimplexNoise2D(seed).octaves(2).spread(12f / scale).scaled(-0.2f, 0.2f))
    ),
    GRADIENT_X((seed, scale) -> new SinNoise(1, 0, 1f / scale, 0)
        .extendY()
        .transformed((x, z) -> MathHelper.clamp(x, -scale / 2f, scale / 2f), (x, z) -> z)
        .add(new SimplexNoise2D(seed).octaves(2).spread(12f / scale).scaled(-0.2f, 0.2f))
    ),
    GRADIENT_Z((seed, scale) -> new SinNoise(1, 0, 1f / scale, 0)
        .extendX()
        .transformed((x, z) -> x, (x, z) -> MathHelper.clamp(z, -scale / 2f, scale / 2f))
        .add(new SimplexNoise2D(seed).octaves(2).spread(12f / scale).scaled(-0.2f, 0.2f))
    ),
    NOISE((seed, scale) -> new SimplexNoise2D(seed).octaves(4).spread(12f / scale));

    private final ILayerTypeFactory factory;

    NoiseLayerType(ILayerTypeFactory factory)
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
