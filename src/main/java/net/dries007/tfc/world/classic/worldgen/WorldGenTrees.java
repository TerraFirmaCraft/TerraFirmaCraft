/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.biomes.BiomeTFC;
import net.dries007.tfc.objects.biomes.BiomesTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.trees.ITreeGenerator;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class WorldGenTrees implements IWorldGenerator
{

    public static PlacementSettings getDefaultSettings()
    {
        return new PlacementSettings()
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(false)
            .setReplacedBlock(Blocks.AIR);
    }

    public static PlacementSettings getRandomSettings(Random rand)
    {
        return getDefaultSettings()
            //.setMirror(Mirror.values()[rand.nextInt(Mirror.values().length)])
            .setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)]);
    }

    public static boolean canGenerateTree(World world, BlockPos pos, Template tree, PlacementSettings settings, Wood treeType)
    {
        // Check if ground is flat enough
        final int radius = treeType.getRadiusForGrowth();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                if (!world.getBlockState(pos.add(x, 1, z)).getMaterial().isReplaceable())
                    return false;
            }
        }
        return true;
    }

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

        for(int i = 0; i < 3; i++)
        {
            final int x = chunkX * 16 + random.nextInt(16) + 8;
            final int z = chunkZ * 16 + random.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x,0,z));
            if (world.getBlockState(pos).getBlock() != Blocks.AIR || !BlocksTFC.isSoil(world.getBlockState(pos.down())))
                continue;

            final Wood tree;
            final float rng = random.nextFloat();

            if(rng < 0.5)
                tree = chunkData.getTree1(pos);
            else if(rng < 0.8)
                tree = chunkData.getTree2(pos);
            else
                tree = chunkData.getTree3(pos);

            ITreeGenerator gen = tree.getTreeGenerator();
            gen.generateTree(manager, world, pos, tree, random);

            //world.setBlockState(new BlockPos(x, 180, z), BlockLogTFC.get(tree1).getDefaultState(), 2);
            //world.setBlockState(new BlockPos(x + 1, 180, z), BlockLogTFC.get(tree2).getDefaultState(), 2);
            //world.setBlockState(new BlockPos(x + 2, 180, z), BlockLogTFC.get(tree3).getDefaultState(), 2);

            //generateTree(manager, world, pos, tree, random);

            //for(int j = 0; j < 5; j++)
            //    world.setBlockState(pos.up(j), BlockLogTFC.get(tree).getDefaultState(), 2);

        }

    }
}
