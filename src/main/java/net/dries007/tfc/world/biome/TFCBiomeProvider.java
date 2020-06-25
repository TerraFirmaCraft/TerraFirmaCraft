/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.stream.Collectors;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.area.LazyArea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.layer.TFCLayerUtil;

public class TFCBiomeProvider extends BiomeProvider
{
    public static final Codec<TFCBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("seed").forGetter(c -> c.seed),
        LayerSettings.CODEC.forGetter(TFCBiomeProvider::getLayerSettings),
        RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(c -> c.biomeRegistry)
    ).apply(instance, TFCBiomeProvider::new));

    // Set from codec
    private final long seed;
    private final LayerSettings layerSettings;
    private final Registry<Biome> biomeRegistry;

    private final LazyArea biomeArea;
    private ChunkDataProvider chunkDataProvider;

    public TFCBiomeProvider(long seed, LayerSettings layerSettings, Registry<Biome> biomeRegistry)
    {
        super(TFCBiomes.getAllKeys().stream().map(biomeRegistry::getOrThrow).collect(Collectors.toList()));

        this.seed = seed;
        this.layerSettings = layerSettings;
        this.biomeRegistry = biomeRegistry;

        this.biomeArea = TFCLayerUtil.createOverworldBiomeLayer(seed, layerSettings).make();
    }

    public LayerSettings getLayerSettings()
    {
        return layerSettings;
    }

    public void setChunkDataProvider(ChunkDataProvider chunkDataProvider)
    {
        this.chunkDataProvider = chunkDataProvider;
    }

    @Override
    protected Codec<TFCBiomeProvider> codec()
    {
        return CODEC;
    }

    @Override
    public TFCBiomeProvider withSeed(long seedIn)
    {
        return new TFCBiomeProvider(seedIn, layerSettings, biomeRegistry);
    }

    /**
     * In {@link net.minecraft.world.biome.BiomeContainer}, we can see that the x, y, z positions are not absolute block coordinates.
     * Rather, since MC now samples biomes once per 4x4x4 area basis, these are not accurate for our chunk data purposes
     * So, we need to make them accurate.
     */
    @Override
    public Biome getNoiseBiome(int biomeCoordX, int biomeCoordY, int biomeCoordZ)
    {
        final ChunkPos chunkPos = new ChunkPos(biomeCoordX >> 2, biomeCoordZ >> 2);
        final BlockPos pos = chunkPos.getWorldPosition();
        final ChunkData data = chunkDataProvider.get(chunkPos, ChunkData.Status.CLIMATE);
        final BiomeVariants variants = TFCLayerUtil.getFromLayerId(biomeArea.get(biomeCoordX, biomeCoordZ));
        final BiomeExtension extension = variants.get(data.getAverageTemp(pos), data.getRainfall(pos));
        return biomeRegistry.getOrThrow(extension.getRegistryKey());
    }

    public static final class LayerSettings
    {
        private static final MapCodec<LayerSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(0, 100).fieldOf("ocean_percent").forGetter(LayerSettings::getOceanPercent),
            Codec.intRange(1, 10).fieldOf("rock_layer_scale").forGetter(LayerSettings::getRockLayerScale)
        ).apply(instance, LayerSettings::new));

        private final int oceanPercent;
        private final int rockLayerScale;

        public LayerSettings()
        {
            this(45, 7);
        }

        public LayerSettings(int oceanPercent, int rockLayerScale)
        {
            this.oceanPercent = oceanPercent;
            this.rockLayerScale = rockLayerScale;
        }

        public int getOceanPercent()
        {
            return oceanPercent;
        }

        public int getRockLayerScale()
        {
            return rockLayerScale;
        }
    }
}