/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.LongFunction;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.INoise2D;

public class BiomeBuilder<V extends BiomeVariants>
{
    public static BiomeBuilder<CarvingBiomeVariants> carving(BiomeVariants parent, LongFunction<Pair<INoise2D, INoise2D>> carvingNoiseFactory)
    {
        return new BiomeBuilder<>(b -> new CarvingBiomeVariants(parent, carvingNoiseFactory));
    }

    public static BiomeBuilder<BiomeVariants> builder(LongFunction<INoise2D> noiseFactory)
    {
        BiomeBuilder<BiomeVariants> builder = new BiomeBuilder<>(b -> new BiomeVariants(b.noiseFactory, b.smallGroup, b.largeGroup, b.salty, b.volcanic, b.volcanoFrequency, b.volcanoBasaltHeight));
        builder.noiseFactory = noiseFactory;
        return builder;
    }

    private final Function<BiomeBuilder<V>, V> factory;
    private final List<BiomeDictionary.Type> dictionaryTypes;

    private LongFunction<INoise2D> noiseFactory;
    private BiomeVariants.SmallGroup smallGroup;
    private BiomeVariants.LargeGroup largeGroup;
    private boolean salty;
    private boolean volcanic;
    private int volcanoFrequency;
    private int volcanoBasaltHeight;

    private BiomeBuilder(Function<BiomeBuilder<V>, V> factory)
    {
        this.factory = factory;
        this.dictionaryTypes = new ArrayList<>();

        noiseFactory = seed -> null;
        smallGroup = BiomeVariants.SmallGroup.BODY;
        largeGroup = BiomeVariants.LargeGroup.LAND;
        salty = false;
        volcanic = false;
        volcanoFrequency = 0;
        volcanoBasaltHeight = 0;
    }

    public BiomeBuilder<V> types(BiomeDictionary.Type... types)
    {
        this.dictionaryTypes.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeBuilder<V> group(BiomeVariants.SmallGroup group)
    {
        this.smallGroup = group;
        return this;
    }

    public BiomeBuilder<V> group(BiomeVariants.LargeGroup group)
    {
        this.largeGroup = group;
        return this;
    }

    public BiomeBuilder<V> salty()
    {
        this.salty = true;
        return this;
    }

    public BiomeBuilder<V> volcanoes(int frequency, int baseHeight, int scaleHeight, int volcanoBasaltHeight)
    {
        this.volcanic = true;
        this.volcanoFrequency = frequency;
        this.volcanoBasaltHeight = TFCChunkGenerator.SEA_LEVEL + volcanoBasaltHeight;

        final LongFunction<INoise2D> baseNoiseFactory = this.noiseFactory;
        this.noiseFactory = seed -> BiomeNoise.addVolcanoes(seed, baseNoiseFactory.apply(seed), frequency, baseHeight, scaleHeight);
        return this;
    }

    public void registerTypes(RegistryKey<Biome> biome)
    {
        dictionaryTypes.forEach(type -> BiomeDictionary.addTypes(biome, type));
    }

    public V build()
    {
        return factory.apply(this);
    }
}
