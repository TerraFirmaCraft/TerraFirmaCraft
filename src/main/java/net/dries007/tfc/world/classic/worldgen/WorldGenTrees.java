/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
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

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.biomes.BiomeTFC;
import net.dries007.tfc.objects.biomes.BiomesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
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

        Biome b = world.getBiome(chunkBlockPos);
        //noinspection ConstantConditions
        if(!(b instanceof BiomeTFC) || b == BiomesTFC.OCEAN || b == BiomesTFC.DEEP_OCEAN || b == BiomesTFC.LAKE || b == BiomesTFC.RIVER) return;

        TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();

        for(int i = 0; i < 3; i++)
        {
            final int x = chunkX * 16 + random.nextInt(16) + 8;
            final int z = chunkZ * 16 + random.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x,0,z));

            final Wood tree;
            final float rng = random.nextFloat();

            if(rng < 0.5)
                tree = chunkData.getTree1(pos);
            else if(rng < 0.8)
                tree = chunkData.getTree2(pos);
            else
                tree = chunkData.getTree3(pos);

            //world.setBlockState(new BlockPos(x, 180, z), BlockLogTFC.get(tree1).getDefaultState(), 2);
            //world.setBlockState(new BlockPos(x + 1, 180, z), BlockLogTFC.get(tree2).getDefaultState(), 2);
            //world.setBlockState(new BlockPos(x + 2, 180, z), BlockLogTFC.get(tree3).getDefaultState(), 2);

            //generateTree(manager, world, pos, Wood.ASH, random);

            for(int j = 0; j < 5; j++)
                world.setBlockState(pos.up(j), BlockLogTFC.get(tree).getDefaultState(), 2);

        }

    }

    public void generateTree(TemplateManager manager, World world, BlockPos pos, Wood tree, Random rand)
    {
        // todo: change this to use some number somewhere of tree structure names
        ResourceLocation loc = new ResourceLocation(Constants.MOD_ID, tree.name().toLowerCase() + "/" + "ashlarge1");
        Template template = manager.getTemplate(world.getMinecraftServer(), loc);
        BlockPos size = template.getSize();
        pos = pos.add(-size.getX() / 2, 0, -size.getY() / 2);
        PlacementSettings settings = new PlacementSettings()
            .setMirror(Mirror.values()[rand.nextInt(Mirror.values().length)])
            .setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)])
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(false)
            .setReplacedBlock(Blocks.AIR);

        if (canGenerateTree(world, pos, template))
        {
            template.addBlocksToWorld(world, pos, settings);
        }
    }

    private boolean canGenerateTree(World world, BlockPos pos, Template tree)
    {
        // Check if ground is flat enough
        // Check if no blocks will be overwritten
        //tree.getDataBlocks()
        return false;
    }
}
