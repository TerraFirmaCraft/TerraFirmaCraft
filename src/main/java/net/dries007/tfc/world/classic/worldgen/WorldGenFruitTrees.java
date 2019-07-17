/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class WorldGenFruitTrees implements IWorldGenerator
{
    private static final List<IFruitTree> TREES = new ArrayList<>();

    public static void register(IFruitTree tree)
    {
        TREES.add(tree);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (random.nextInt(Math.max(40 - TREES.size(), 20)) == 0)
        {
            IFruitTree tree = TREES.get(random.nextInt(TREES.size()));
            BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);

            float temperature = ClimateTFC.getAverageBiomeTemp(world, chunkBlockPos);
            float rainfall = ChunkDataTFC.getRainfall(world, chunkBlockPos);

            if (tree.isValidConditions(temperature, rainfall))
            {
                TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();

                final int x = (chunkX << 4) + random.nextInt(16) + 8;
                final int z = (chunkZ << 4) + random.nextInt(16) + 8;
                final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

                if (tree.getGenerator().canGenerateTree(world, pos, tree))
                {
                    tree.getGenerator().generateTree(manager, world, pos, tree, random);
                }
            }
        }
    }
}
