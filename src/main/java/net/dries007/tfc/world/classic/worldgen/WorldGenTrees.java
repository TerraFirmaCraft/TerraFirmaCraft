/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.List;
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
        final List<Tree> trees = chunkData.getValidTrees();
        final float diversity = chunkData.getFloraDiversity();

        // This is to avoid giant regions of no trees whatsoever.
        // It will create sparse trees ( < 1 per chunk) by averaging the climate data to make it more temperate
        // The thought is in very harsh conditions, a few trees might survive outside their typical temperature zone
        if (trees.isEmpty())
        {
            if (random.nextFloat() < 0.3f)
                return;

            Tree extra = chunkData.getSparseGenTree();
            if (extra != null)
            {
                final int x = chunkX * 16 + random.nextInt(16) + 8;
                final int z = chunkZ * 16 + random.nextInt(16) + 8;
                final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
                if (world.getBlockState(pos).getBlock() == Blocks.AIR && BlocksTFC.isSoil(world.getBlockState(pos.down())))
                    extra.makeTree(manager, world, pos, random);
            }
            return;
        }

        final int spawnTries = 2 + (int) (chunkData.getFloraDensity() * 12f);
        for (int i = 0; i < spawnTries; i++)
        {
            final int x = chunkX * 16 + random.nextInt(16) + 8;
            final int z = chunkZ * 16 + random.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x,0,z));
            if (world.getBlockState(pos).getBlock() != Blocks.AIR || !BlocksTFC.isSoil(world.getBlockState(pos.down())))
                continue;

            final Tree tree = getTree(trees, diversity, random);

            tree.makeTree(manager, world, pos, random);
        }
    }

    private Tree getTree(List<Tree> trees, float diversity, Random random)
    {
        final int maxTrees = Math.min(trees.size(), Math.min(5, (int) (1 + diversity * 5f)));
        trees = trees.subList(0, maxTrees);
        if (maxTrees == 1)
            return trees.get(0);
        if (random.nextFloat() < 0.8f - diversity * 0.4f)
            return trees.get(0);
        return trees.get(1 + random.nextInt(maxTrees - 1));
    }
}
