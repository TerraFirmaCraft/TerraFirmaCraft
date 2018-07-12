/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.ChunkGenTFC;

public class WorldGenTrees implements IWorldGenerator
{

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;

        TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();

        for(int i = 0; i < 3; i++)
        {
            int x = chunkX * 16 + random.nextInt(16) + 8;
            int z = chunkZ * 16 + random.nextInt(16) + 8;
            BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x,0,z));

            generateTree(manager, world, pos, Wood.ASH);

        }

    }

    private void generateTree(TemplateManager manager, World world, BlockPos pos, Wood tree)
    {
        ResourceLocation loc = new ResourceLocation(Constants.MOD_ID, tree.name().toLowerCase()+"/"+"1");
        Template template = manager.getTemplate(world.getMinecraftServer(), loc);
        BlockPos size = template.getSize();
        PlacementSettings settings = new PlacementSettings()
            .setMirror(Mirror.NONE)
            .setRotation(Rotation.NONE)
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(false);
        template.addBlocksToWorld(world, pos.add(-size.getX() / 2, 0, -size.getY() / 2), settings);
    }
}
