/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class LowlandsBiome extends TFCBiome
{
    public LowlandsBiome()
    {
        super(new Builder().category(Category.PLAINS));
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.55f).scaled(TFCConfig.COMMON.seaLevel.get() - 6, TFCConfig.COMMON.seaLevel.get() + 7).flattened(TFCConfig.COMMON.seaLevel.get() - 4, TFCConfig.COMMON.seaLevel.get() + 3);
    }
}
