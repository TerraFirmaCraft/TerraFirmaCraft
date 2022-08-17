/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.surface.builder.SurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SurfaceBuilderFactory;
import org.jetbrains.annotations.Nullable;

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

    @Nullable private List<HolderSet<PlacedFeature>> flattenedFeatures;
    @Nullable private Set<PlacedFeature> flattenedFeatureSet;
    @Nullable private Biome prevBiome;

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

    public List<HolderSet<PlacedFeature>> getFlattenedFeatures(Biome biome)
    {
        if (biome != prevBiome)
        {
            // These fields are cached on a per-biome-instance (aka per registry) basis
            // But extensions are globally instanced.
            // So we need to invalidate when we're querying from a different biome instance.
            prevBiome = biome;
            flattenedFeatures = null;
        }
        if (flattenedFeatures == null)
        {
            flattenedFeatures = Helpers.flattenTopLevelMultipleFeature(biome.getGenerationSettings());
            flattenedFeatureSet = flattenedFeatures.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet());
        }
        return flattenedFeatures;
    }

    public Set<PlacedFeature> getFlattenedFeatureSet(Biome biome)
    {
        getFlattenedFeatures(biome);
        return Objects.requireNonNull(flattenedFeatureSet);
    }

    public enum Group
    {
        LAND, OCEAN, RIVER, LAKE;

        public static final int SIZE = Group.values().length;
    }
}