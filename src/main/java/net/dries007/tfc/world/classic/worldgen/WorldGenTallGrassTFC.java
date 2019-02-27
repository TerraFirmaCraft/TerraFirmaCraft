/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockTallGrassTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class WorldGenTallGrassTFC extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, position) < 0) return false;

        IBlockState tallGrassState = BlocksTFC.TALL_GRASS.getDefaultState().withProperty(BlockTallGrassTFC.TYPE, BlocksTFC.TALL_GRASS.getBiomePlantType(worldIn, position));

        for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position))
        {
            position = position.down();
        }

        for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 4; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && BlocksTFC.TALL_GRASS.canBlockStay(worldIn, blockpos, tallGrassState) && !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH))
            {
                worldIn.setBlockState(blockpos, tallGrassState, 2);
            }
        }

        if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, position) > 20)
        {
            for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 8; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

                if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 254) && Blocks.DOUBLE_PLANT.canPlaceBlockAt(worldIn, blockpos) && !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH) && worldIn.canSeeSky(blockpos))
                {
                    BlocksTFC.DOUBLE_TALL_GRASS.placeAt(worldIn, blockpos, BlocksTFC.DOUBLE_TALL_GRASS.getBiomePlantType(worldIn, blockpos), 2);
                }
            }
        }

        return true;
    }
}
