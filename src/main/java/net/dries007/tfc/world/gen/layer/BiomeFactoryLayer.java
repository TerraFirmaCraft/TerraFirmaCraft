/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import net.dries007.tfc.world.biome.TFCBiome;

public class BiomeFactoryLayer
{
    private final LazyArea lazyArea;

    public BiomeFactoryLayer(IAreaFactory<LazyArea> lazyAreaFactoryIn)
    {
        this.lazyArea = lazyAreaFactoryIn.make();
    }

    public TFCBiome[] getBiomes(int startX, int startZ, int xSize, int zSize)
    {
        TFCBiome[] biomes = new TFCBiome[xSize * zSize];

        for (int x = 0; x < zSize; ++x)
        {
            for (int z = 0; z < xSize; ++z)
            {
                int value = lazyArea.getValue(startX + z, startZ + x);
                biomes[z + x * xSize] = getBiome(value);
                // todo: apply variants from climate
            }
        }
        return biomes;
    }

    public TFCBiome getBiome(int x, int z)
    {
        return getBiome(lazyArea.getValue(x, z));
    }

    private TFCBiome getBiome(int id)
    {
        return (TFCBiome) ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getValue(id);
    }
}
