/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import javax.annotation.Nonnull;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

import static net.dries007.tfc.world.gen.TFCOverworldChunkGenerator.SEA_LEVEL;

public class LowlandsBiome extends TFCBiome
{
    public LowlandsBiome()
    {
        super(new Builder().category(Category.PLAINS));

        TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.55f).scaled(SEA_LEVEL - 6, SEA_LEVEL + 7).flattened(SEA_LEVEL - 4, SEA_LEVEL + 3);
    }
}
