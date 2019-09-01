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

public class PlainsBiome extends TFCBiome
{
    private final float minHeight;
    private final float maxHeight;

    public PlainsBiome(float minHeight, float maxHeight)
    {
        super(new Builder().category(Category.PLAINS).surfaceBuilder(TFCSurfaceBuilders.DEFAULT_NORMAL));
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.17f).scaled(SEA_LEVEL + minHeight, SEA_LEVEL + maxHeight);
    }
}
