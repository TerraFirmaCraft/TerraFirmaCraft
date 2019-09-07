/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.world.climate.ClimateType;
import net.dries007.tfc.world.noise.INoise2D;

public abstract class TFCBiome extends Biome
{
    // todo: replace with actual blocks
    protected static final BlockState SALT_WATER = Blocks.WATER.getDefaultState();
    protected static final BlockState FRESH_WATER = Blocks.WATER.getDefaultState();

    protected TFCBiome(Builder builder)
    {
        this(ClimateType.NORMAL_NORMAL, builder);
    }

    protected TFCBiome(ClimateType climateType, Builder builder)
    {
        super(builder
            // Climate properties (mostly unused, here for compatibility)
            .precipitation(climateType.getRainType()).temperature(climateType.getTemperature()).downfall(climateType.getDownfall())
            // Unused properties - the colors will be set dynamically by temperature / rainfall layers
            .depth(0).scale(0).waterColor(4159204).waterFogColor(329011)
            // Unused for now, may be used by variant biomes
            .parent(null)
        );
    }

    @Nonnull
    public abstract INoise2D createNoiseLayer(long seed);

    @Nonnull
    public BlockState getWaterState()
    {
        return FRESH_WATER;
    }
}
