/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongFunction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.builder.SurfaceBuilderFactory;
import net.dries007.tfc.world.surface.builder.VolcanoesSurfaceBuilder;
import org.jetbrains.annotations.Nullable;

public class BiomeBuilder
{
    public static BiomeBuilder builder()
    {
        return new BiomeBuilder();
    }

    @Nullable private LongFunction<Noise2D> heightNoiseFactory;
    @Nullable private LongFunction<BiomeNoiseSampler> noiseFactory;
    @Nullable private SurfaceBuilderFactory surfaceBuilderFactory;

    private DoubleUnaryOperator aquiferSurfaceHeight;
    private BiomeExtension.Group group;
    private boolean salty;
    private boolean volcanic;
    private int volcanoFrequency;
    private int volcanoBasaltHeight;
    private boolean spawnable;

    private BiomeBuilder()
    {
        aquiferSurfaceHeight = height -> height;
        group = BiomeExtension.Group.LAND;
        salty = false;
        volcanic = false;
        volcanoFrequency = 0;
        volcanoBasaltHeight = 0;
        spawnable = false;
    }

    public BiomeBuilder heightmap(LongFunction<Noise2D> heightNoiseFactory)
    {
        this.heightNoiseFactory = heightNoiseFactory;
        this.noiseFactory = seed -> BiomeNoiseSampler.fromHeightNoise(heightNoiseFactory.apply(seed));
        return this;
    }

    public BiomeBuilder surface(SurfaceBuilderFactory surfaceBuilderFactory)
    {
        this.surfaceBuilderFactory = surfaceBuilderFactory;
        return this;
    }

    public BiomeBuilder carving(BiFunction<Long, Noise2D, BiomeNoiseSampler> carvingNoiseFactory)
    {
        Objects.requireNonNull(heightNoiseFactory, "Height noise must not be null");
        final LongFunction<Noise2D> baseHeightNoiseFactory = heightNoiseFactory;
        this.noiseFactory = seed -> carvingNoiseFactory.apply(seed, baseHeightNoiseFactory.apply(seed));
        this.aquiferSurfaceHeight = height -> TFCChunkGenerator.SEA_LEVEL_Y - 16; // Expect sea level carving to restrict aquifers
        return this;
    }

    public BiomeBuilder noise(LongFunction<BiomeNoiseSampler> noiseFactory)
    {
        this.noiseFactory = noiseFactory;
        return this;
    }

    public BiomeBuilder aquiferHeightOffset(final double delta)
    {
        return aquiferHeight(height -> height + delta);
    }

    public BiomeBuilder aquiferHeight(DoubleUnaryOperator aquiferSurfaceHeight)
    {
        this.aquiferSurfaceHeight = aquiferSurfaceHeight;
        return this;
    }

    public BiomeBuilder group(BiomeExtension.Group group)
    {
        this.group = group;
        return this;
    }

    public BiomeBuilder salty()
    {
        this.salty = true;
        return this;
    }

    public BiomeBuilder spawnable()
    {
        this.spawnable = true;
        return this;
    }

    public BiomeBuilder volcanoes(int frequency, int baseHeight, int scaleHeight, int volcanoBasaltHeight)
    {
        this.volcanic = true;
        this.volcanoFrequency = frequency;
        this.volcanoBasaltHeight = TFCChunkGenerator.SEA_LEVEL_Y + volcanoBasaltHeight;

        assert heightNoiseFactory != null : "volcanoes must be called after setting a heightmap";
        assert surfaceBuilderFactory != null : "volcanoes must be called after setting a surface builder";

        final LongFunction<Noise2D> baseHeightNoiseFactory = this.heightNoiseFactory;
        this.heightNoiseFactory = seed -> BiomeNoise.addVolcanoes(seed, baseHeightNoiseFactory.apply(seed), frequency, baseHeight, scaleHeight);
        this.noiseFactory = seed -> BiomeNoiseSampler.fromHeightNoise(heightNoiseFactory.apply(seed));

        this.surfaceBuilderFactory = VolcanoesSurfaceBuilder.create(surfaceBuilderFactory);

        return this;
    }


    public BiomeExtension build(ResourceKey<Biome> key)
    {
        assert noiseFactory != null : "missing noise / heightmap";
        assert surfaceBuilderFactory != null : "missing surface builder";

        return new BiomeExtension(key, noiseFactory, surfaceBuilderFactory, aquiferSurfaceHeight, group, salty, volcanic, volcanoFrequency, volcanoBasaltHeight, spawnable);
    }
}
