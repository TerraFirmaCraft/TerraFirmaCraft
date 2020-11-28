package net.dries007.tfc.world.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongFunction;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.INoise2D;

public class BiomeBuilder
{
    public static BiomeBuilder carving(BiomeVariants parent, LongFunction<Pair<INoise2D, INoise2D>> carvingNoiseFactory)
    {
        BiomeBuilder builder = new BiomeBuilder();
        builder.parent = parent;
        builder.carvingNoiseFactory = carvingNoiseFactory;
        return builder;
    }

    public static BiomeBuilder builder(LongFunction<INoise2D> noiseFactory)
    {
        BiomeBuilder builder = new BiomeBuilder();
        builder.noiseFactory = noiseFactory;
        return builder;
    }

    private final List<BiomeDictionary.Type> dictionaryTypes;

    private BiomeVariants parent;
    private LongFunction<INoise2D> noiseFactory;
    private LongFunction<Pair<INoise2D, INoise2D>> carvingNoiseFactory;
    private BiomeVariants.SmallGroup smallGroup;
    private BiomeVariants.LargeGroup largeGroup;
    private boolean salty;
    private boolean volcanic;
    private int volcanoFrequency;
    private int volcanoBasaltHeight;

    private BiomeBuilder()
    {
        parent = null;
        noiseFactory = seed -> null;
        carvingNoiseFactory = seed -> null;
        dictionaryTypes = new ArrayList<>();
        smallGroup = BiomeVariants.SmallGroup.BODY;
        largeGroup = BiomeVariants.LargeGroup.LAND;
        salty = false;
        volcanic = false;
        volcanoFrequency = 0;
        volcanoBasaltHeight = 0;
    }

    public BiomeBuilder types(BiomeDictionary.Type... types)
    {
        this.dictionaryTypes.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeBuilder group(BiomeVariants.SmallGroup group)
    {
        this.smallGroup = group;
        return this;
    }

    public BiomeBuilder group(BiomeVariants.LargeGroup group)
    {
        this.largeGroup = group;
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

        final LongFunction<INoise2D> baseNoiseFactory = this.noiseFactory;
        this.noiseFactory = seed -> BiomeNoise.addVolcanoes(seed, baseNoiseFactory.apply(seed), frequency, baseHeight, scaleHeight);
        return this;
    }

    public void registerTypes(RegistryKey<Biome> biome)
    {
        dictionaryTypes.forEach(type -> BiomeDictionary.addTypes(biome, type));
    }

    @SuppressWarnings("unchecked")
    public <V extends BiomeVariants> V build()
    {
        if (parent != null)
        {
            return (V) new CarvingBiomeVariants(parent, carvingNoiseFactory);
        }
        return (V) new BiomeVariants(noiseFactory, smallGroup, largeGroup, salty, volcanic, volcanoFrequency, volcanoBasaltHeight);
    }
}
