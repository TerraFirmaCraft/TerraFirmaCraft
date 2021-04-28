/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
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
    private final Group group;
    private final boolean salty;
    private final boolean volcanic;
    private final int volcanoFrequency;
    private final int volcanoBasaltHeight;

    protected BiomeVariants(BiomeVariants parent)
    {
        this(parent.noiseFactory, parent.group, parent.salty, parent.volcanic, parent.volcanoFrequency, parent.volcanoBasaltHeight);
    }

    public BiomeVariants(LongFunction<INoise2D> noiseFactory, Group group, boolean salty, boolean volcanic, int volcanoFrequency, int volcanoBasaltHeight)
    {
        this.noiseFactory = noiseFactory;
        this.group = group;
        this.salty = salty;
        this.volcanic = volcanic;
        this.volcanoFrequency = volcanoFrequency;
        this.volcanoBasaltHeight = volcanoBasaltHeight;

        extensions = new EnumMap<>(BiomeTemperature.class);
        for (BiomeTemperature temperature : BiomeTemperature.values())
        {
            extensions.put(temperature, new EnumMap<>(BiomeRainfall.class));
        }
    }

    public Group getGroup()
    {
        return group;
    }

    public boolean isSalty()
    {
        return salty;
    }

    public boolean isVolcanic()
    {
        return volcanic;
    }

    public float getVolcanoChance()
    {
        return 1f / volcanoFrequency;
    }

    public int getVolcanoBasaltHeight()
    {
        return volcanoBasaltHeight;
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

    public enum Group
    {
        LAND, OCEAN, RIVER, LAKE;

        public static final int SIZE = Group.values().length;
    }
}