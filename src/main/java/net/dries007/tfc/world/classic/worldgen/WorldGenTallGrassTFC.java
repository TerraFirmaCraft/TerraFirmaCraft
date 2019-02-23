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

public class WorldGenTallGrassTFC extends WorldGenerator
{
    private final IBlockState tallGrassState;
    private BlockTallGrassTFC.EnumGrassType plantType;

    public WorldGenTallGrassTFC(BlockTallGrassTFC.EnumGrassType type)
    {
        this.plantType = type;
        this.tallGrassState = BlocksTFC.TALL_GRASS.getDefaultState().withProperty(BlockTallGrassTFC.TYPE, type);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int maxPlantGen;
        for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position))
        {
            position = position.down();
        }

        switch (this.plantType.getName())
        {
            case "desert_grass":
                maxPlantGen = 2;
                break;
            case "lush_grass":
                maxPlantGen = 160;
                break;
            case "sparse_grass":
                maxPlantGen = 64;
                break;
            default:
                maxPlantGen = 128;
                break;
        }

        for (int i = 0; i < 128; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && BlocksTFC.TALL_GRASS.canBlockStay(worldIn, blockpos, this.tallGrassState) && !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH))
            {
                worldIn.setBlockState(blockpos, this.tallGrassState, 2);
            }
        }

        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 254) && Blocks.DOUBLE_PLANT.canPlaceBlockAt(worldIn, blockpos) && !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH))
            {
                BlocksTFC.DOUBLE_TALL_GRASS.placeAt(worldIn, blockpos, this.plantType, 2);
            }
        }

        return true;
    }
}
