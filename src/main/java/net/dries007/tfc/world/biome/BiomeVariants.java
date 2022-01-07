/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongFunction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.surface.builder.SurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SurfaceBuilderFactory;

/**
 * This is a version of {@link RegistryObject} for biomes.
 * Since we have variants in both temperature and rainfall, we use this as the "biome main type" object.
 */
public class BiomeVariants
{
    private final Map<BiomeTemperature, Map<BiomeRainfall, BiomeExtension>> extensions;
    private final LongFunction<BiomeNoiseSampler> noiseFactory;
    private final DoubleUnaryOperator aquiferSurfaceHeight;
    private final SurfaceBuilderFactory surfaceBuilderFactory;
    private final Group group;
    private final boolean salty;
    private final boolean volcanic;
    private final int volcanoFrequency;
    private final int volcanoBasaltHeight;
    private final boolean spawnable;

    BiomeVariants(LongFunction<BiomeNoiseSampler> noiseFactory, SurfaceBuilderFactory surfaceBuilderFactory, DoubleUnaryOperator aquiferSurfaceHeight, Group group, boolean salty, boolean volcanic, int volcanoFrequency, int volcanoBasaltHeight, boolean spawnable)
    {
        this.noiseFactory = noiseFactory;
        this.surfaceBuilderFactory = surfaceBuilderFactory;
        this.aquiferSurfaceHeight = aquiferSurfaceHeight;
        this.group = group;
        this.salty = salty;
        this.volcanic = volcanic;
        this.volcanoFrequency = volcanoFrequency;
        this.volcanoBasaltHeight = volcanoBasaltHeight;
        this.spawnable = spawnable;

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

    public boolean isRiver()
    {
        return group == Group.RIVER;
    }

    public boolean isShore()
    {
        return this == TFCBiomes.SHORE;
    }

    public boolean isSalty()
    {
        return salty;
    }

    public boolean isVolcanic()
    {
        return volcanic;
    }

    public boolean isSpawnable()
    {
        return spawnable;
    }

    public float getVolcanoChance()
    {
        return 1f / volcanoFrequency;
    }

    public int getVolcanoBasaltHeight()
    {
        return volcanoBasaltHeight;
    }

    public double getAquiferSurfaceHeight(double height)
    {
        return aquiferSurfaceHeight.applyAsDouble(height);
    }

    public BiomeNoiseSampler createNoiseSampler(long seed)
    {
        return noiseFactory.apply(seed);
    }

    public SurfaceBuilder createSurfaceBuilder(long seed)
    {
        return surfaceBuilderFactory.apply(seed);
    }

    public BiomeExtension createBiomeExtension(ResourceKey<Biome> key, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        final BiomeExtension ex = new BiomeExtension(key, temperature, rainfall, this);
        extensions.get(temperature).put(rainfall, ex);
        return ex;
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