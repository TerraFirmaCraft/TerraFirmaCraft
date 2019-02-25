/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.common.BiomeDictionary;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockCreepingPlantTFC;
import net.dries007.tfc.objects.blocks.plants.BlockDoublePlantTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;

public class WorldGenPlantTFC extends WorldGenerator
{
    private Plant plant;

    public void setGeneratedPlant(Plant plantIn)
    {
        this.plant = plantIn;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {

        if (plant.getPlantType() == Plant.PlantType.CREEPINGPLANT)
        {
            BlockCreepingPlantTFC plantBlock = BlockCreepingPlantTFC.get(plant, plant.getPlantType());
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 64; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 255) && plantBlock.canBlockStay(worldIn, blockpos, state) && rand.nextInt() < 10 && !BlocksTFC.isSand(worldIn.getBlockState(blockpos.down())))
                {
                    if (plant.getRegistryName().toString().equalsIgnoreCase("tfc:moss"))
                    {
                        if (worldIn.getLight(blockpos) <= 10)
                            worldIn.setBlockState(blockpos, state, 2);
                    }
                    else
                    {
                        worldIn.setBlockState(blockpos, state, 2);
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.PLANT)
        {
            BlockPlantTFC plantBlock = BlockPlantTFC.get(plant, plant.getPlantType());
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 255) && plantBlock.canBlockStay(worldIn, blockpos, state))
                {
                    worldIn.setBlockState(blockpos, state, 2);
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.DOUBLEPLANT)
        {
            BlockDoublePlantTFC plantBlock = BlockDoublePlantTFC.get(plant, plant.getPlantType());

            boolean flag = false;

            for (int i = 0; i < 16; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 254) && plantBlock.canPlaceBlockAt(worldIn, blockpos))
                {
                    plantBlock.placeAt(worldIn, blockpos, 2);
                    flag = true;
                }
            }

            return flag;
        }

        return true;
    }
}