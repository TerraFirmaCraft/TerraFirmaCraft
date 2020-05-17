/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.HashSet;
import java.util.List;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.world.TFCGenerationSettings;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.layer.TFCLayerUtil;

public class TFCBiomeProvider extends BiomeProvider
{
    private final BiomeFactory biomeFactory;
    private final Lazy<List<Biome>> spawnBiomes;

    private ChunkDataProvider chunkDataProvider;

    public TFCBiomeProvider(TFCGenerationSettings settings)
    {
        super(new HashSet<>(TFCBiomes.getBiomes()));
        WorldInfo worldInfo = settings.getWorldInfo();

        this.biomeFactory = new BiomeFactory(TFCLayerUtil.createOverworldBiomeLayer(worldInfo.getSeed(), settings));
        this.spawnBiomes = Lazy.of(TFCBiomes::getSpawnBiomes);
    }

    public void setChunkDataProvider(ChunkDataProvider chunkDataProvider)
    {
        this.chunkDataProvider = chunkDataProvider;
    }

    @Override
    public List<Biome> getBiomesToSpawnIn()
    {
        return spawnBiomes.get();
    }

    @Override
    public boolean hasStructure(Structure<?> structureIn)
    {
        return this.hasStructureCache.computeIfAbsent(structureIn, structure -> {
            for (Biome biome : biomes) // valid biomes
            {
                if (biome.hasStructure(structure))
                {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * In {@link net.minecraft.world.biome.BiomeContainer}, we can see that the x, y, z positions are not absolute block coordinates.
     * Rather, since MC now samples biomes once per 4x4x4 area basis, these are not accurate for our chunk data purposes
     * So, we need to make them accurate.
     */
    @Override
    public Biome getNoiseBiome(int x, int y, int z)
    {
        TFCBiome baseBiome = biomeFactory.getBiome(x, z);
        ChunkData data = chunkDataProvider.get(new ChunkPos(x >> 2, z >> 2), ChunkData.Status.CLIMATE, false);
        return baseBiome.getVariantHolder().get(data.getAverageTemp(), data.getRainfall()).get();
    }
}
