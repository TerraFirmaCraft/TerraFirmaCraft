/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.chunkdata.ChunkDataGenerator;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.RegionChunkDataGenerator;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.river.MidpointFractal;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RegionBiomeSource extends BiomeSource implements BiomeSourceExtension
{
    public static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCE = DeferredRegister.create(Registries.BIOME_SOURCE, MOD_ID);
    public static final Codec<RegionBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Settings.CODEC.forGetter(BiomeSourceExtension::settings),
        RegistryOps.retrieveGetter(Registries.BIOME)
    ).apply(instance, RegionBiomeSource::new));

    static
    {
        BIOME_SOURCE.register("overworld", () -> RegionBiomeSource.CODEC);
    }

    private final HolderGetter<Biome> biomeRegistry;
    private final Settings settings;

    private RegionGenerator regionGenerator;
    private ConcurrentArea<BiomeExtension> biomeLayer;
    private ChunkDataProvider chunkDataProvider;

    public RegionBiomeSource(Settings settings, HolderGetter<Biome> biomeRegistry)
    {
        this.settings = settings;
        this.biomeRegistry = biomeRegistry;
    }

    @Override
    public Holder<Biome> getBiome(int quartX, int quartZ)
    {
        return biomeRegistry.getOrThrow(getBiomeExtensionWithRiver(quartX, quartZ).key());
    }

    @Override
    public BiomeExtension getBiomeExtensionWithRiver(int quartX, int quartZ)
    {
        // This is heuristic, and doesn't need to be fully accurate
        final BiomeExtension biome = getBiomeExtensionNoRiver(quartX, quartZ);
        if (biome.hasRivers())
        {
            final int gridX = Units.quartToGrid(quartX);
            final int gridZ = Units.quartToGrid(quartZ);

            final float exactGridX = Units.quartToGridExact(quartX);
            final float exactGridZ = Units.quartToGridExact(quartZ);
            final RegionPartition partition = regionGenerator.getOrCreatePartition(gridX, gridZ);
            final RegionPartition.Point partitionPoint = partition.get(gridX, gridZ);

            for (RiverEdge edge : partitionPoint.rivers())
            {
                // maybeIntersect will skip the more expensive calculation if it fails
                final MidpointFractal fractal = edge.fractal();
                if (fractal.maybeIntersect(exactGridX, exactGridZ, 0.1f) && fractal.intersect(exactGridX, exactGridZ, 0.01f))
                {
                    return TFCBiomes.RIVER;
                }
            }
        }
        return biome;
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

    public RegionPartition getRegionPartition(int blockX, int blockZ)
    {
        return regionGenerator.getOrCreatePartition(Units.blockToGrid(blockX), Units.blockToGrid(blockZ));
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public Settings settings()
    {
        return settings;
    }

    @Override
    public void initRandomState(ServerLevel level)
    {
        final RandomSource random = new XoroshiroRandomSource(level.getSeed());
        final RegionGenerator regionGenerator = new RegionGenerator(random.nextLong());

        final AreaFactory factory = TFCLayers.createRegionBiomeLayer(regionGenerator, random.nextLong());
        final ChunkDataGenerator chunkDataGenerator = new RegionChunkDataGenerator(random.nextLong(), settings.rockLayerSettings(), regionGenerator);

        this.regionGenerator = regionGenerator;
        this.biomeLayer = new ConcurrentArea<>(factory, TFCLayers::getFromLayerId);
        this.chunkDataProvider = new ChunkDataProvider(chunkDataGenerator, settings.rockLayerSettings());
    }

    @Override
    protected Codec<? extends BiomeSource> codec()
    {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes()
    {
        return TFCBiomes.getAllKeys().stream().map(biomeRegistry::getOrThrow);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, @Nullable Climate.Sampler sampler)
    {
        return getBiome(quartX, quartZ);
    }
}
