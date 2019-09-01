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

public class BadlandsBiome extends TFCBiome
{
    public BadlandsBiome()
    {
        super(new Builder().category(Category.MESA).surfaceBuilder(TFCSurfaceBuilders.DEFAULT_THIN));

        TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Normal flat noise, lowered by inverted power-ridge noise, looks like badlands
        final INoise2D ridgeNoise = new SimplexNoise2D(seed).octaves(4).ridged().spread(0.04f).map(x -> 1.3f * -(x > 0 ? (float) Math.pow(x, 3.2f) : 0.5f * x)).scaled(-1f, 0.3f, -1f, 1f).terraces(16).scaled(-20, 0);
        return new SimplexNoise2D(seed).octaves(6).spread(0.08f).scaled(SEA_LEVEL + 22, SEA_LEVEL + 32).add(ridgeNoise);
    }
}
