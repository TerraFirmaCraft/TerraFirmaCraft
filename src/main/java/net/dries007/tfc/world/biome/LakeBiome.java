/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import javax.annotation.Nonnull;

import net.dries007.tfc.world.gen.surfacebuilders.TFCSurfaceBuilders;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

import static net.dries007.tfc.world.gen.TFCOverworldChunkGenerator.SEA_LEVEL;

public class LakeBiome extends TFCBiome
{
    public LakeBiome()
    {
        super(new Builder().category(Category.RIVER).surfaceBuilder(TFCSurfaceBuilders.DEFAULT_THIN));

        TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.2f).scaled(SEA_LEVEL - 8, SEA_LEVEL - 3);
    }
}
