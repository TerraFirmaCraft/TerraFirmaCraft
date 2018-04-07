package net.dries007.tfc.world.classic.worldgen;

import net.dries007.tfc.util.OreSpawnData;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldGenOre implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;

        for (OreSpawnData data : OreSpawnData.ORE_SPAWN_DATA)
        {
            int veinSize;
            int veinAmount;
            int height;
            int diameter;
            switch (data.size)
            {
                case SMALL:
                    veinSize = 20;
                    veinAmount = 30;
                    height = 5;
                    diameter = 40;
                    break;
                case MEDIUM:
                    veinSize = 30;
                    veinAmount = 40;
                    height = 10;
                    diameter = 60;
                    break;
                case LARGE:
                    veinSize = 60;
                    veinAmount = 45;
                    height = 20;
                    diameter = 80;
                    break;
                default:
                    throw new RuntimeException("Enum constants not constant");
            }

            switch (data.type)
            {
                case DEFAULT:
                    createOre(null, 0, null, data.rarity, veinSize, veinAmount, height, diameter, data.densityVertical, data.densityHorizontal, world, random, chunkX, chunkZ, data.minY, data.maxY);
                    break;
                case VEINS:
                    createOreVein(null, 0, null, data.rarity, veinSize, veinAmount, height, diameter, data.densityVertical, data.densityHorizontal, world, random, chunkX, chunkZ, data.minY, data.maxY);
                    break;
            }
        }
    }

    private static void createOre(Block block, int j, Map<Block, List<Integer>> layers, int rarity, int veinSize, int veinAmount, int height, int diameter, int vDensity, int hDensity, World world, Random rand, int chunkX, int chunkZ, int min, int max)
    {

    }

    private static void createOreVein(Block block, int j, Map<Block, List<Integer>> layers, int rarity, int veinSize, int veinAmount, int height, int diameter, int vDensity, int hDensity, World world, Random rand, int chunkX, int chunkZ, int min, int max)
    {

    }
}
