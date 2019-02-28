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

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.*;

import static net.dries007.tfc.world.classic.ChunkGenTFC.FRESH_WATER;

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
        else if (plant.getPlantType() == Plant.PlantType.PLANT || plant.getPlantType() == Plant.PlantType.DESERTPLANT)
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
        else if (plant.getPlantType() == Plant.PlantType.LILYPAD)
        {
            for (int i = 0; i < 32; ++i)
            {
                final BlockPos p2 = position.add(rand.nextInt(8) - rand.nextInt(8),
                    rand.nextInt(4) - rand.nextInt(4),
                    rand.nextInt(8) - rand.nextInt(8));

                if (worldIn.isAirBlock(p2) && BlockLilyPadTFC.get(plant, plant.getPlantType()).canPlaceBlockAt(worldIn, p2) &&
                    worldIn.getBlockState(p2.add(0, -1, 0)) == FRESH_WATER &&
                    worldIn.getBlockState(p2.add(0, -2, 0)) != FRESH_WATER) // todo: make this a little less harsh
                {
                    worldIn.setBlockState(p2, BlockLilyPadTFC.get(plant, plant.getPlantType()).getDefaultState(), 0x02);
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.CACTUS)
        {
            BlockCactusTFC plantBlock = BlockCactusTFC.get(plant, plant.getPlantType());
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 10; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

                if (worldIn.isAirBlock(blockpos))
                {
                    int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                    for (int k = 0; k < j; ++k)
                    {
                        if (plantBlock.canBlockStay(worldIn, blockpos, state))
                        {
                            worldIn.setBlockState(blockpos.up(k), plantBlock.getDefaultState(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}