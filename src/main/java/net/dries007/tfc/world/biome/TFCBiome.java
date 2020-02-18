/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.world.gen.surfacebuilders.ISurfaceBuilder;
import net.dries007.tfc.world.noise.INoise2D;

import static net.dries007.tfc.world.gen.TFCOverworldChunkGenerator.SEA_LEVEL;

public abstract class TFCBiome extends Biome
{
    // todo: replace with actual blocks
    protected static final BlockState SALT_WATER = Blocks.WATER.getDefaultState();
    protected static final BlockState FRESH_WATER = Blocks.WATER.getDefaultState();

    protected TFCBiome(Builder builder)
    {
        super(builder
            // Unused properties - the colors will be set dynamically by temperature / rainfall layers
            .depth(0).scale(0).waterColor(4159204).waterFogColor(329011).precipitation(RainType.RAIN).temperature(1.0f).downfall(1.0f)
            // Unused for now, may be used by variant biomes
            .parent(null)
            // Unused as we do a much more complex surface builder
            .surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG))
        );
    }

    @Nonnull
    public abstract INoise2D createNoiseLayer(long seed);

    @Nonnull
    public ISurfaceBuilder getTFCSurfaceBuilder()
    {
        return ISurfaceBuilder.DEFAULT;
    }

    @Nonnull
    public BlockState getWaterState()
    {
        return FRESH_WATER;
    }

    /**
     * This is the default height for the boundary between rock layers
     * It is adjusted by a minor noise field, not dependent on surface height
     */
    public int getDefaultRockHeight()
    {
        return SEA_LEVEL / 2;
    }
}
