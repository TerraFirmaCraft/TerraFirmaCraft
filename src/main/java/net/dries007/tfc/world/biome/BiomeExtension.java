/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.surface.builder.SurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SurfaceBuilderFactory;

/**
 * Represents all extra data TFC attaches to biomes, which is not present in the {@link Biome} class, nor is it data driven.
 */
public final class BiomeExtension
{
    private final ResourceKey<Biome> key;

    @Nullable private final LongFunction<BiomeNoiseSampler> noiseFactory;
    private final AquiferLookahead aquiferSurfaceHeight;
    private final SurfaceBuilderFactory surfaceBuilderFactory;

    private final BiomeBlendType biomeBlendType;
    private final RiverBlendType riverBlendType;
    private final boolean salty;
    private final boolean volcanic;
    private final int volcanoRarity;
    private final int volcanoBasaltHeight;
    private final boolean spawnable;
    private final boolean rivers;
    private final boolean shore;
    private final boolean sandyRiverShores;

    @Nullable private List<HolderSet<PlacedFeature>> flattenedFeatures;
    @Nullable private Set<PlacedFeature> flattenedFeatureSet;
    @Nullable private Biome prevBiome;

    BiomeExtension(ResourceKey<Biome> key, @Nullable LongFunction<BiomeNoiseSampler> noiseFactory, SurfaceBuilderFactory surfaceBuilderFactory, AquiferLookahead aquiferSurfaceHeight, BiomeBlendType biomeBlendType, RiverBlendType riverBlendType, boolean salty, boolean volcanic, int volcanoRarity, int volcanoBasaltHeight, boolean spawnable, boolean rivers, boolean shore, boolean sandyRiverShores)
    {
        this.key = key;
        this.noiseFactory = noiseFactory;
        this.surfaceBuilderFactory = surfaceBuilderFactory;
        this.aquiferSurfaceHeight = aquiferSurfaceHeight;
        this.biomeBlendType = biomeBlendType;
        this.riverBlendType = riverBlendType;
        this.salty = salty;
        this.volcanic = volcanic;
        this.volcanoRarity = volcanoRarity;
        this.volcanoBasaltHeight = volcanoBasaltHeight;
        this.spawnable = spawnable;
        this.rivers = rivers;
        this.shore = shore;
        this.sandyRiverShores = sandyRiverShores;
    }

    public ResourceKey<Biome> key()
    {
        return key;
    }

    public BiomeBlendType biomeBlendType()
    {
        return biomeBlendType;
    }

    public RiverBlendType riverBlendType()
    {
        return riverBlendType;
    }

    public boolean hasSandyRiverShores()
    {
        return sandyRiverShores;
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

    public boolean hasRivers()
    {
        return rivers;
    }

    public boolean isShore()
    {
        return shore;
    }

    public int getVolcanoRarity()
    {
        return volcanoRarity;
    }

    public int getVolcanoBasaltHeight()
    {
        return volcanoBasaltHeight;
    }

    public double getAquiferSurfaceHeight(BiomeNoiseSampler sampler, int x, int z)
    {
        return aquiferSurfaceHeight.getHeight(sampler, x, z);
    }

    @Nullable
    public BiomeNoiseSampler createNoiseSampler(long seed)
    {
        return noiseFactory != null ? noiseFactory.apply(seed) : null;
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
            flattenedFeatures = biome.getGenerationSettings().features();
            flattenedFeatureSet = flattenedFeatures.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet());
        }
        return flattenedFeatures;
    }

    public Set<PlacedFeature> getFlattenedFeatureSet(Biome biome)
    {
        getFlattenedFeatures(biome);
        return Objects.requireNonNull(flattenedFeatureSet);
    }
}