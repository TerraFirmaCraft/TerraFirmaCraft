/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.FeatureCycleDetector;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.TFCChunkDataGenerator;
import net.dries007.tfc.world.settings.ClimateSettings;
import net.dries007.tfc.world.settings.RockLayerSettings;

import static net.dries007.tfc.TerraFirmaCraft.*;

public abstract class TFCBiomeSource extends BiomeSource implements BiomeSourceExtension, RiverSource
{
    public static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCE = DeferredRegister.create(Registry.BIOME_SOURCE_REGISTRY, MOD_ID);

    static
    {
        BIOME_SOURCE.register("overworld", () -> LegacyBiomeSource.CODEC);
        BIOME_SOURCE.register("continental", () -> RegionBiomeSource.CODEC);
    }

    public static TFCBiomeSource defaultBiomeSource(long seed, Registry<Biome> biomeRegistry)
    {
        final Settings settings = new Settings(seed, 8_000, 0, 0, RockLayerSettings.getDefault(), ClimateSettings.DEFAULT, ClimateSettings.DEFAULT);
        return new LegacyBiomeSource(settings, biomeRegistry);
    }

    protected final Settings settings;
    protected final Registry<Biome> biomeRegistry;
    protected final Supplier<List<StepFeatureData>> customFeaturesPerStep;
    protected final ChunkDataProvider chunkDataProvider;


    protected TFCBiomeSource(Settings settings, Registry<Biome> biomeRegistry, TFCChunkDataGenerator chunkDataGenerator, Collection<ResourceKey<Biome>> allBiomes)
    {
        this(settings, biomeRegistry, chunkDataGenerator, allBiomes.stream().map(biomeRegistry::getHolderOrThrow).toList());
    }

    private TFCBiomeSource(Settings settings, Registry<Biome> biomeRegistry, TFCChunkDataGenerator chunkDataGenerator, List<Holder<Biome>> allBiomes)
    {
        super(allBiomes);

        this.settings = settings;

        this.biomeRegistry = biomeRegistry;
        this.customFeaturesPerStep = Suppliers.memoize(() -> FeatureCycleDetector.buildFeaturesPerStep(allBiomes));

        this.chunkDataProvider = new ChunkDataProvider(chunkDataGenerator, settings.rockLayerSettings());
    }

    @Override
    public Settings settings()
    {
        return settings;
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, @Nullable Climate.Sampler sampler)
    {
        return getNoiseBiome(quartX, quartZ);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartZ)
    {
        return biomeRegistry.getHolderOrThrow(getNoiseBiomeVariants(quartX, quartZ).key());
    }

    @Override
    public Holder<Biome> getBiome(BiomeExtension variants)
    {
        return biomeRegistry.getHolderOrThrow(variants.key());
    }

    @Override
    public List<StepFeatureData> featuresPerStep()
    {
        return customFeaturesPerStep.get();
    }

    @Override
    public abstract TFCBiomeSource withSeed(long seed);

    @Override
    @Nullable
    public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int blockX, int blockY, int blockZ, int maxRadius, int step, Predicate<Holder<Biome>> biome, Random random, boolean findClosest, @Nullable Climate.Sampler sampler)
    {
        final int minQuartX = QuartPos.fromBlock(blockX);
        final int minQuartZ = QuartPos.fromBlock(blockZ);
        final int maxQuartRadius = QuartPos.fromBlock(maxRadius);

        Pair<BlockPos, Holder<Biome>> pair = null;
        int count = 0;
        for (int radius = findClosest ? 0 : maxQuartRadius; radius <= maxQuartRadius; radius += step)
        {
            for (int dz = -radius; dz <= radius; dz += step)
            {
                final boolean atZEdge = Math.abs(dz) == radius;
                for (int dx = -radius; dx <= radius; dx += step)
                {
                    if (findClosest)
                    {
                        boolean atXEdge = Math.abs(dx) == radius;
                        if (!atXEdge && !atZEdge)
                        {
                            continue;
                        }
                    }

                    final int x = minQuartX + dx, z = minQuartZ + dz;
                    Holder<Biome> found = getNoiseBiome(x, z);
                    if (biome.test(found))
                    {
                        if (pair == null || random.nextInt(count + 1) == 0)
                        {
                            BlockPos pos = new BlockPos(QuartPos.toBlock(x), blockY, QuartPos.toBlock(z));
                            if (findClosest)
                            {
                                return Pair.of(pos, found);
                            }
                            pair = Pair.of(pos, found);
                        }
                        count++;
                    }
                }
            }
        }
        return pair;
    }

}