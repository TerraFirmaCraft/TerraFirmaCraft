/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class HillsBiome extends TFCBiome
{
    private final float height;

    public HillsBiome(float height)
    {
        super(new Builder().category(Category.PLAINS));
        this.height = height;
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.06f).scaled(TFCConfig.COMMON.seaLevel.get() - 5, TFCConfig.COMMON.seaLevel.get() + height);
    }
}
