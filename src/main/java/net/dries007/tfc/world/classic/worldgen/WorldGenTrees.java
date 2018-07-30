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

import net.dries007.tfc.TerraFirmaCraft;
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
        final float rain = chunkData.getRainfall(chunkBlockPos);
        final float evt = chunkData.getEvt(chunkBlockPos);
        final float temp = ClimateTFC.getBioTemperature(world.getSeed(), chunkBlockPos.getZ(), rain);

        for(int i = 0; i < 10; i++)
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

            if (tree.minEVT > evt || tree.maxEVT < evt || tree.minRain > rain || tree.maxRain < rain) // tree.minTemp > temp || tree.maxTemp < temp ||
                continue;

            tree.makeTree(manager, world, pos, random);
        }
    }
}
