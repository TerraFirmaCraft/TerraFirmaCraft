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
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.*;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

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

                if (plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos), ChunkDataTFC.getRainfall(worldIn, blockpos), worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    (!worldIn.provider.isNether() || blockpos.getY() < 255) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state) &&
                    rand.nextInt() < 10 && !BlocksTFC.isSand(worldIn.getBlockState(blockpos.down())))
                {
                    worldIn.setBlockState(blockpos, state, 2);
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

                if (plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos), ChunkDataTFC.getRainfall(worldIn, blockpos), worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    (!worldIn.provider.isNether() || blockpos.getY() < 255) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state))
                {
                    worldIn.setBlockState(blockpos, state, 2);
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.DESERTPLANT)
        {
            BlockPlantTFC plantBlock = BlockPlantTFC.get(plant, plant.getPlantType());
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 128; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(16) - rand.nextInt(16), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(16) - rand.nextInt(16));

                if (plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos), ChunkDataTFC.getRainfall(worldIn, blockpos), worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH) &&
                    (!worldIn.provider.isNether() || blockpos.getY() < 255) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state))
                {
                    worldIn.setBlockState(blockpos, state, 2);
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.DOUBLEPLANT)
        {
            BlockDoublePlantTFC plantBlock = BlockDoublePlantTFC.get(plant, plant.getPlantType());
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    if (plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k)), ChunkDataTFC.getRainfall(worldIn, blockpos.up(k)), worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        (!worldIn.provider.isNether() || blockpos.up(k).getY() < 254) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        worldIn.setBlockState(blockpos.up(k), state, 2);
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.LILYPAD)
        {
            BlockLilyPadTFC plantBlock = BlockLilyPadTFC.get(plant, plant.getPlantType());
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                final BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8),
                    rand.nextInt(4) - rand.nextInt(4),
                    rand.nextInt(8) - rand.nextInt(8));

                if (plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos), ChunkDataTFC.getRainfall(worldIn, blockpos), worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    (!worldIn.provider.isNether() || blockpos.getY() < 254) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos) &&
                    worldIn.getBlockState(blockpos.add(0, -1, 0)) == FRESH_WATER &&
                    worldIn.getBlockState(blockpos.add(0, -2, 0)) != FRESH_WATER) // todo: make this a little less harsh
                {
                    worldIn.setBlockState(blockpos, state, 2);
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

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    if (plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k)), ChunkDataTFC.getRainfall(worldIn, blockpos.up(k)), worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        (!worldIn.provider.isNether() || blockpos.up(k).getY() < 254) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        worldIn.setBlockState(blockpos.up(k), state, 2);
                    }
                }
            }
        }

        return true;
    }
}