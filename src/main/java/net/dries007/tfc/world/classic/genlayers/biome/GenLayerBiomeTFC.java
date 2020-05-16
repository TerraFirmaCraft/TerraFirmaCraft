/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import java.util.Arrays;
import java.util.Objects;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerBiomeTFC extends GenLayerTFC
{
    private final int[] biomes = BiomesTFC.getWorldGenBiomes().stream().mapToInt(Biome::getIdForBiome).toArray();

    public GenLayerBiomeTFC(long seed, GenLayer parent)
    {
        super(seed);
        this.parent = parent;
        if (ConfigTFC.General.DEBUG.debugWorldGenSafe)
        {
            TerraFirmaCraft.getLog().info("Worldgen biome list (ints): {}", biomes);
            TerraFirmaCraft.getLog().info("Worldgen biome list (names): {}", (Object) Arrays.stream(biomes).mapToObj(Biome::getBiomeForId).map(Objects::toString).toArray());
        }
    }

    @Override
    public int[] getInts(int x, int y, int sizeX, int sizeY)
    {
        int[] ints = parent.getInts(x, y, sizeX, sizeY);
        int[] out = IntCache.getIntCache(sizeX * sizeY);

        for (int yy = 0; yy < sizeY; ++yy)
        {
            for (int xx = 0; xx < sizeX; ++xx)
            {
                initChunkSeed(xx + x, yy + y);
                int id = ints[xx + yy * sizeX];
                if (isOceanicBiome(id)) out[xx + yy * sizeX] = id;
                else out[xx + yy * sizeX] = biomes[nextInt(biomes.length)];
            }
        }
        return out;
    }
}
