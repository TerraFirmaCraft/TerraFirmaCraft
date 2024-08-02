/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.stream.Stream;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.Units;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RegionBiomeSource extends BiomeSource implements BiomeSourceExtension
{
    public static final MapCodec<RegionBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RegistryOps.retrieveGetter(Registries.BIOME)
    ).apply(instance, RegionBiomeSource::new));

    private final HolderGetter<Biome> biomeRegistry;

    private RegionGenerator regionGenerator;
    private ConcurrentArea<BiomeExtension> biomeLayer;

    public RegionBiomeSource(HolderGetter<Biome> biomeRegistry)
    {
        this.biomeRegistry = biomeRegistry;
    }

    @Override
    public BiomeExtension getBiomeExtensionNoRiver(int quartX, int quartZ)
    {
        return biomeLayer.get(quartX, quartZ);
    }

    @Override
    public Holder<Biome> getBiomeFromExtension(BiomeExtension extension)
    {
        return biomeRegistry.getOrThrow(extension.key());
    }

    public RegionPartition.Point getPartition(int blockX, int blockZ)
    {
        return regionGenerator.getOrCreatePartitionPoint(Units.blockToGrid(blockX), Units.blockToGrid(blockZ));
    }

    @Override
    public void initRandomState(RegionGenerator regionGenerator, ConcurrentArea<BiomeExtension> biomeLayer)
    {
        this.regionGenerator = regionGenerator;
        this.biomeLayer = biomeLayer;
    }

    @Override
    public BiomeSourceExtension copy()
    {
        return new RegionBiomeSource(biomeRegistry);
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec()
    {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes()
    {
        return TFCBiomes.REGISTRY.stream()
            .map(e -> biomeRegistry.getOrThrow(e.key()));
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, @Nullable Climate.Sampler sampler)
    {
        return getBiome(quartX, quartZ);
    }
}
