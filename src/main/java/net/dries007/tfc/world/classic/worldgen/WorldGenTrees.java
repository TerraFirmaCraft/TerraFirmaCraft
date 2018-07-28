/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.biomes.BiomeTFC;
import net.dries007.tfc.objects.biomes.BiomesTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class WorldGenTrees implements IWorldGenerator
{

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;

        final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        ChunkDataTFC chunkData = ChunkDataTFC.get(world, chunkBlockPos);
        if (!chunkData.isInitialized()) return;

        final Biome b = world.getBiome(chunkBlockPos);
        //noinspection ConstantConditions
        if(!(b instanceof BiomeTFC) || b == BiomesTFC.OCEAN || b == BiomesTFC.DEEP_OCEAN || b == BiomesTFC.LAKE || b == BiomesTFC.RIVER) return;

        final TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();
        final float temp = ClimateTFC.getTemp(world, chunkBlockPos);
        final float rain = chunkData.getRainfall(chunkBlockPos);
        final float evt = chunkData.getEvt(chunkBlockPos);

        for(int i = 0; i < 3; i++)
        {
            final int x = chunkX * 16 + random.nextInt(16) + 8;
            final int z = chunkZ * 16 + random.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x,0,z));
            if (world.getBlockState(pos).getBlock() != Blocks.AIR || !BlocksTFC.isSoil(world.getBlockState(pos.down())))
                continue;

            final Tree tree;
            final float rng = random.nextFloat();

            if(rng < 0.5)
                tree = chunkData.getTree1();
            else if(rng < 0.8)
                tree = chunkData.getTree2();
            else
                tree = chunkData.getTree3();

            if (tree.minTemp > temp || tree.maxTemp < temp || tree.minEVT > evt || tree.maxEVT < evt || tree.minRain > rain || tree.maxRain < rain)
                continue;

            tree.makeTree(manager, world, pos, random);

            //world.setBlockState(new BlockPos(x, 180, z), BlockLogTFC.get(tree1).getDefaultState(), 2);
            //world.setBlockState(new BlockPos(x + 1, 180, z), BlockLogTFC.get(tree2).getDefaultState(), 2);
            //world.setBlockState(new BlockPos(x + 2, 180, z), BlockLogTFC.get(tree3).getDefaultState(), 2);

            //generateTree(manager, world, pos, tree, random);

            //for(int j = 0; j < 5; j++)
            //    world.setBlockState(pos.up(j), BlockLogTFC.get(tree).getDefaultState(), 2);

        }

    }
}
