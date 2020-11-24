/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.LongFunction;

import net.minecraftforge.fml.RegistryObject;

import net.dries007.tfc.world.noise.INoise2D;

/**
 * This is a version of {@link RegistryObject} for biomes.
 * Since we have variants in both temperature and rainfall, we use this as the "biome main type" object.
 * To get the variant holder from the biome, use {@link TFCBiome#getVariants()}
 * To get the biome from the variants, use one of the {@link BiomeVariants#get()} methods.
 */
public class BiomeVariants
{
    private final Map<BiomeTemperature, Map<BiomeRainfall, BiomeExtension>> extensions;
    private final LongFunction<INoise2D> noiseFactory;
    private final SmallGroup smallGroup;
    private final LargeGroup largeGroup;
    private final boolean salty;

    public BiomeVariants(LongFunction<INoise2D> noiseFactory, SmallGroup smallGroup, LargeGroup largeGroup, boolean salty)
    {
        this.noiseFactory = noiseFactory;
        this.smallGroup = smallGroup;
        this.largeGroup = largeGroup;
        this.salty = salty;

        extensions = new EnumMap<>(BiomeTemperature.class);
        for (BiomeTemperature temperature : BiomeTemperature.values())
        {
            extensions.put(temperature, new EnumMap<>(BiomeRainfall.class));
        }
    }

    public LargeGroup getLargeGroup()
    {
        return largeGroup;
    }

    public SmallGroup getSmallGroup()
    {
        return smallGroup;
    }

    public boolean isSalty()
    {
        return salty;
    }

    public INoise2D createNoiseLayer(long seed)
    {
        return noiseFactory.apply(seed);
    }

    public void put(BiomeTemperature temperature, BiomeRainfall rainfall, BiomeExtension extension)
    {
        extensions.get(temperature).put(rainfall, extension);
    }

    /**
     * @return the biome instance of the specified temperature / rainfall
     */
    public BiomeExtension get(BiomeTemperature temp, BiomeRainfall rain)
    {
        return extensions.get(temp).get(rain);
    }

    public enum LargeGroup
    {
        LAND, OCEAN, RIVER, LAKE;

        public static final int SIZE = LargeGroup.values().length;
    }

    public enum SmallGroup
    {
        BODY, RIVER;

        public static final int SIZE = SmallGroup.values().length;
    }
}