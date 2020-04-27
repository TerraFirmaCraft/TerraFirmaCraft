/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;

public class ShoreBiome extends TFCBiome
{
    private final boolean isStone;

    public ShoreBiome(boolean isStone)
    {
        super(new Biome.Builder().category(Category.BEACH));
        this.isStone = isStone;
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return (x, z) -> TFCConfig.COMMON.seaLevel.get();
    }
}
