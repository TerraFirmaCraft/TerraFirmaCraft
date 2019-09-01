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

public class OceanBiome extends TFCBiome
{
    private final float depthMin, depthMax;

    public OceanBiome(boolean isDeep)
    {
        super(new Builder().category(Category.OCEAN).surfaceBuilder(TFCSurfaceBuilders.DEFAULT_NORMAL));

        if (isDeep)
        {
            this.depthMin = SEA_LEVEL - 36;
            this.depthMax = SEA_LEVEL - 10;
        }
        else
        {
            this.depthMin = SEA_LEVEL - 24;
            this.depthMax = SEA_LEVEL - 6;
        }

        TFCDefaultBiomeFeatures.addOceanCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Uses domain warping to achieve a swirly hills effect
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.04f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, depthMin, depthMax);
    }
}
