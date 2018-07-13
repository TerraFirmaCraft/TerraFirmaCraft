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
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.Wood;
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

        TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();
        Wood tree1 = chunkData.getTree1();
        Wood tree2 = chunkData.getTree2();
        Wood tree3 = chunkData.getTree3();
        //chunkData.getTree1(chunkX, chunkZ);

        for(int i = 0; i < 3; i++)
        {
            int x = chunkX * 16 + random.nextInt(16) + 8;
            int z = chunkZ * 16 + random.nextInt(16) + 8;
            BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x,0,z));

            world.setBlockState(new BlockPos(x, 180, z), BlockLogTFC.get(tree1).getDefaultState(), 2);
            world.setBlockState(new BlockPos(x + 1, 180, z), BlockLogTFC.get(tree2).getDefaultState(), 2);
            world.setBlockState(new BlockPos(x + 2, 180, z), BlockLogTFC.get(tree3).getDefaultState(), 2);
            generateTree(manager, world, pos, Wood.ASH, random);

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
