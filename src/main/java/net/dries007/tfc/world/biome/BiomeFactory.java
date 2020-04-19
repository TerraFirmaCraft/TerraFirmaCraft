/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class BiomeFactory
{
    public static TFCBiome getBiome(int id)
    {
        return (TFCBiome) ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getValue(id);
    }

    private final LazyArea lazyArea;

    public BiomeFactory(IAreaFactory<LazyArea> lazyAreaFactoryIn)
    {
        this.lazyArea = lazyAreaFactoryIn.make();
    }

    public TFCBiome[] getBiomes(int startX, int startZ, int xSize, int zSize)
    {
        TFCBiome[] biomes = new TFCBiome[xSize * zSize];

        for (int x = 0; x < xSize; ++x)
        {
            for (int z = 0; z < zSize; ++z)
            {
                int value = lazyArea.getValue(startX + x, startZ + z);
                biomes[x + z * xSize] = getBiome(value);
            }
        }
        return biomes;
    }

    public TFCBiome getBiome(int x, int z)
    {
        return getBiome(lazyArea.getValue(x, z));
    }
}
