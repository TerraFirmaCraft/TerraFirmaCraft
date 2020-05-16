/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.noise.INoise2D;

public abstract class TFCBiome extends Biome
{
    // todo: replace with actual blocks
    protected static final BlockState SALT_WATER = Blocks.WATER.getDefaultState();
    protected static final BlockState FRESH_WATER = Blocks.WATER.getDefaultState();

    // Set during setup, after surface builders are available
    protected ConfiguredSurfaceBuilder<? extends ISurfaceBuilderConfig> lazySurfaceBuilder;

    private BiomeVariantHolder variantHolder;

    protected TFCBiome(Builder builder, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(builder
            // Unused
            .depth(0).scale(0).parent(null)
            // Provide reasonable defaults, TFC doesn't use these
            .waterColor(temperature.getWaterColor())
            .waterFogColor(temperature.getWaterFogColor())
            .precipitation(Climate.getDefaultRainType(temperature, rainfall))
            .temperature(temperature.getTemperature())
            .downfall(rainfall.getDownfall())
            // Since this is a registry object, we just do them all later for consistency
            .surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG))
        );
    }

    public void setVariantHolder(BiomeVariantHolder variantHolder)
    {
        this.variantHolder = variantHolder;
    }

    public BiomeVariantHolder getVariants()
    {
        return variantHolder;
    }

    public abstract INoise2D createNoiseLayer(long seed);

    public BlockState getWaterState()
    {
        return FRESH_WATER;
    }

    public <C extends ISurfaceBuilderConfig> void setSurfaceBuilder(SurfaceBuilder<C> surfaceBuilder, C config)
    {
        setSurfaceBuilder(new ConfiguredSurfaceBuilder<>(surfaceBuilder, config));
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        getSurfaceBuilder().setSeed(seed);
        getSurfaceBuilder().buildSurface(random, chunkIn, this, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
    }

    @Override
    public ConfiguredSurfaceBuilder<?> getSurfaceBuilder()
    {
        if (lazySurfaceBuilder == null)
        {
            LOGGER.warn("Surface builder was null when accessed, falling back to vanilla surface builder instead! Biome = {}", this);
            lazySurfaceBuilder = surfaceBuilder;
        }
        return lazySurfaceBuilder;
    }

    public void setSurfaceBuilder(ConfiguredSurfaceBuilder<? extends ISurfaceBuilderConfig> surfaceBuilder)
    {
        this.lazySurfaceBuilder = surfaceBuilder;
    }

    @Override
    public ISurfaceBuilderConfig getSurfaceBuilderConfig()
    {
        return getSurfaceBuilder().getConfig();
    }
}
