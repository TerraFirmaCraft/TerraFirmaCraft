/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

import static net.dries007.tfc.world.gen.TFCOverworldChunkGenerator.SEA_LEVEL;

public class MountainsBiome extends TFCBiome
{
    private final float baseHeight;
    private final float scaleHeight;
    private final boolean isOceanMountains;

    public MountainsBiome(float baseHeight, float scaleHeight, boolean isOceanMountains)
    {
        super(new Builder().category(Category.EXTREME_HILLS));

        this.baseHeight = baseHeight;
        this.scaleHeight = scaleHeight;
        this.isOceanMountains = isOceanMountains;

        TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Power scaled noise, looks like mountains over large area
        final INoise2D mountainNoise = new SimplexNoise2D(seed).octaves(6).spread(0.14f).map(x -> 2.67f * (float) Math.pow(0.5f * (x + 1), 3.2f) - 0.8f);
        return (x, z) -> SEA_LEVEL + baseHeight + scaleHeight * mountainNoise.noise(x, z);
    }

    @Nonnull
    @Override
    public BlockState getWaterState()
    {
        return isOceanMountains ? SALT_WATER : FRESH_WATER;
    }

    @Override
    public int getDefaultRockHeight()
    {
        return isOceanMountains ? SEA_LEVEL + 10 : SEA_LEVEL - 30;
    }
}
