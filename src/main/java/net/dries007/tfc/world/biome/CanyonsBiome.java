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

public class CanyonsBiome extends TFCBiome
{
    private final float minHeight;
    private final float maxHeight;

    public CanyonsBiome(float minHeight, float maxHeight)
    {
        super(new Builder().category(Category.EXTREME_HILLS).surfaceBuilder(TFCSurfaceBuilders.DEFAULT_NORMAL));

        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.2f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, SEA_LEVEL + minHeight, SEA_LEVEL + maxHeight).spread(0.3f);
    }
}
