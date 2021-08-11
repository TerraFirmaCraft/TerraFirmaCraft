/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.LongFunction;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import net.dries007.tfc.world.IBiomeNoiseSampler;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.Noise2D;

public class BiomeBuilder
{
    public static BiomeBuilder builder()
    {
        return new BiomeBuilder();
    }

    private final List<BiomeDictionary.Type> dictionaryTypes;

    @Nullable private LongFunction<Noise2D> heightNoiseFactory;
    @Nullable private LongFunction<IBiomeNoiseSampler> noiseFactory;

    private BiomeVariants.Group group;
    private boolean salty;
    private boolean volcanic;
    private int volcanoFrequency;
    private int volcanoBasaltHeight;

    private BiomeBuilder()
    {
        dictionaryTypes = new ArrayList<>();
        group = BiomeVariants.Group.LAND;
        salty = false;
        volcanic = false;
        volcanoFrequency = 0;
        volcanoBasaltHeight = 0;
    }

    public BiomeBuilder heightmap(LongFunction<Noise2D> heightNoiseFactory)
    {
        this.heightNoiseFactory = heightNoiseFactory;
        this.noiseFactory = seed -> IBiomeNoiseSampler.fromHeightNoise(heightNoiseFactory.apply(seed));
        return this;
    }

    public BiomeBuilder carving(BiFunction<Long, Noise2D, IBiomeNoiseSampler> carvingNoiseFactory)
    {
        Objects.requireNonNull(heightNoiseFactory, "Height noise must not be null");
        final LongFunction<Noise2D> baseHeightNoiseFactory = heightNoiseFactory;
        this.noiseFactory = seed -> carvingNoiseFactory.apply(seed, baseHeightNoiseFactory.apply(seed));
        return this;
    }

    public BiomeBuilder noise(LongFunction<IBiomeNoiseSampler> noiseFactory)
    {
        this.noiseFactory = noiseFactory;
        return this;
    }

    public BiomeBuilder types(BiomeDictionary.Type... types)
    {
        this.dictionaryTypes.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeBuilder group(BiomeVariants.Group group)
    {
        this.group = group;
        return this;
    }

    public BiomeBuilder salty()
    {
        this.salty = true;
        return this;
    }

    public BiomeBuilder volcanoes(int frequency, int baseHeight, int scaleHeight, int volcanoBasaltHeight)
    {
        this.volcanic = true;
        this.volcanoFrequency = frequency;
        this.volcanoBasaltHeight = TFCChunkGenerator.SEA_LEVEL + volcanoBasaltHeight;

        Objects.requireNonNull(heightNoiseFactory, "Height noise must not be null");
        final LongFunction<Noise2D> baseHeightNoiseFactory = this.heightNoiseFactory;
        this.heightNoiseFactory = seed -> BiomeNoise.addVolcanoes(seed, baseHeightNoiseFactory.apply(seed), frequency, baseHeight, scaleHeight);
        this.noiseFactory = seed -> IBiomeNoiseSampler.fromHeightNoise(heightNoiseFactory.apply(seed));
        return this;
    }

    public void registerTypes(ResourceKey<Biome> biome)
    {
        dictionaryTypes.forEach(type -> BiomeDictionary.addTypes(biome, type));
    }

    public BiomeVariants build()
    {
        return new BiomeVariants(Objects.requireNonNull(noiseFactory), group, salty, volcanic, volcanoFrequency, volcanoBasaltHeight);
    }
}
