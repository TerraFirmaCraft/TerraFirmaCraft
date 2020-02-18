/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import javax.annotation.Nonnull;

import net.minecraft.world.biome.Biome;

import net.dries007.tfc.world.gen.surfacebuilders.ISurfaceBuilder;
import net.dries007.tfc.world.noise.INoise2D;

import static net.dries007.tfc.world.gen.TFCOverworldChunkGenerator.SEA_LEVEL;

public class ShoreBiome extends TFCBiome
{
    private final boolean isStone;

    public ShoreBiome(boolean isStone)
    {
        super(new Biome.Builder().category(Category.BEACH));
        this.isStone = isStone;

        TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return (x, z) -> SEA_LEVEL;
    }

    @Nonnull
    @Override
    public ISurfaceBuilder getTFCSurfaceBuilder()
    {
        return isStone ? ISurfaceBuilder.STONE_SHORE : ISurfaceBuilder.SHORE;
    }
}
