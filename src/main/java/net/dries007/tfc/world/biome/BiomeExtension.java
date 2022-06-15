/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.function.DoubleUnaryOperator;
import java.util.function.LongFunction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.surface.builder.SurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SurfaceBuilderFactory;

/**
 * Represents all extra data TFC attaches to biomes, which is not present in the {@link Biome} class, nor is it data driven.
 */
public class BiomeExtension
{
    private final ResourceKey<Biome> key;

    private final LongFunction<BiomeNoiseSampler> noiseFactory;
    private final DoubleUnaryOperator aquiferSurfaceHeight;
    private final SurfaceBuilderFactory surfaceBuilderFactory;

    private final Group group;
    private final boolean salty;
    private final boolean volcanic;
    private final int volcanoRarity;
    private final int volcanoBasaltHeight;
    private final boolean spawnable;

    BiomeExtension(ResourceKey<Biome> key, LongFunction<BiomeNoiseSampler> noiseFactory, SurfaceBuilderFactory surfaceBuilderFactory, DoubleUnaryOperator aquiferSurfaceHeight, Group group, boolean salty, boolean volcanic, int volcanoRarity, int volcanoBasaltHeight, boolean spawnable)
    {
        this.key = key;
        this.noiseFactory = noiseFactory;
        this.surfaceBuilderFactory = surfaceBuilderFactory;
        this.aquiferSurfaceHeight = aquiferSurfaceHeight;
        this.group = group;
        this.salty = salty;
        this.volcanic = volcanic;
        this.volcanoRarity = volcanoRarity;
        this.volcanoBasaltHeight = volcanoBasaltHeight;
        this.spawnable = spawnable;
    }

    public ResourceKey<Biome> key()
    {
        return key;
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

    public int getVolcanoRarity()
    {
        return volcanoRarity;
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

    public enum Group
    {
        LAND, OCEAN, RIVER, LAKE;

        public static final int SIZE = Group.values().length;
    }
}