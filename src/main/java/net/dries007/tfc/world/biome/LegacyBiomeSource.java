/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.chunkdata.TFCChunkDataGenerator;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.Watershed;

public class LegacyBiomeSource extends TFCBiomeSource
{
    public static final Codec<LegacyBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Settings.CODEC.forGetter(BiomeSourceExtension::settings),
        RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(c -> c.biomeRegistry)
    ).apply(instance, LegacyBiomeSource::new));

    protected final ConcurrentArea<BiomeExtension> biomeLayer;
    protected final Watershed.Context watersheds;

    public LegacyBiomeSource(Settings settings, Registry<Biome> biomeRegistry)
    {
        super(settings, biomeRegistry, new TFCChunkDataGenerator(settings), TFCBiomes.getAllKeys());

        final long seed = settings.seed();

        this.watersheds = new Watershed.Context(TFCLayers.createEarlyPlateLayers(seed), seed, 0.5f, 0.8f, 14, 0.2f);
        this.biomeLayer = new ConcurrentArea<>(TFCLayers.createOverworldBiomeLayerWithRivers(seed, watersheds, IArtist.nope(), IArtist.nope()), TFCLayers::getFromLayerId);
    }

    @Override
    public Flow getRiverFlow(int quartX, int quartZ)
    {
        final float scale = 1f / (1 << 7);
        final float x0 = quartX * scale, z0 = quartZ * scale;
        for (MidpointFractal fractal : watersheds.getFractalsByPartition(quartX, quartZ))
        {
            // maybeIntersect will skip the more expensive calculation if it fails
            if (fractal.maybeIntersect(x0, z0, Watershed.RIVER_WIDTH))
            {
                final Flow flow = fractal.intersectWithFlow(x0, z0, Watershed.RIVER_WIDTH);
                if (flow != Flow.NONE)
                {
                    return flow;
                }
            }
        }
        return Flow.NONE;
    }

    @Override
    public BiomeExtension getNoiseBiomeVariants(int quartX, int quartZ)
    {
        return biomeLayer.get(quartX, quartZ);
    }

    @Override
    public LegacyBiomeSource withSeed(long seed)
    {
        return new LegacyBiomeSource(settings.withSeed(seed), biomeRegistry);
    }

    @Override
    protected Codec<LegacyBiomeSource> codec()
    {
        return CODEC;
    }
}
