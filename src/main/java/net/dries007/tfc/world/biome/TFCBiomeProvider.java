/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.WorldInfo;

import net.dries007.tfc.world.TFCGenerationSettings;
import net.dries007.tfc.world.layer.TFCLayerUtil;

public class TFCBiomeProvider extends BiomeProvider
{
    private final BiomeFactory biomeFactory;

    public TFCBiomeProvider(TFCGenerationSettings settings)
    {
        super(new HashSet<>(TFCBiomes.getBiomes()));
        WorldInfo worldInfo = settings.getWorldInfo();

        this.biomeFactory = new BiomeFactory(TFCLayerUtil.createOverworldBiomeLayer(worldInfo.getSeed(), settings));

        // todo: create temperature / rainfall layers, and use them to generate biome permutations
    }

    public TFCBiome[] getBiomes(int x, int z, int width, int length)
    {
        // todo: BiomeContainer?
        return biomeFactory.getBiomes(x, z, width, length);
    }

    @Override
    public boolean hasStructure(Structure<?> structureIn)
    {
        return this.hasStructureCache.computeIfAbsent(structureIn, structure -> {
            for (Biome biome : field_226837_c_) // valid biomes
            {
                if (biome.hasStructure(structure))
                {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public List<Biome> getBiomesToSpawnIn()
    {
        return super.getBiomesToSpawnIn();
    }

    @Override
    public Set<BlockState> getSurfaceBlocks()
    {
        // todo: handle seperately since surface builders aren't going to used
        return super.getSurfaceBlocks();
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z)
    {
        // todo: handle this?
        return biomeFactory.getBiome(x, z);
    }
}
