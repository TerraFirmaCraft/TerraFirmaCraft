/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
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
import static net.dries007.tfc.world.classic.ChunkGenTFC.SALT_WATER;

@ParametersAreNonnullByDefault
public class WorldGenPlantTFC extends WorldGenerator
{
    private Plant plant;

    public void setGeneratedPlant(Plant plantIn)
    {
        this.plant = plantIn;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (plant.getIsClayMarking()) return false;
        if (plant.getPlantType() == Plant.PlantType.SHORT_GRASS)
        {
            BlockShortGrassTFC plantBlock = BlockShortGrassTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 4; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockShortGrassTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.TALL_GRASS)
        {
            BlockTallGrassTFC plantBlock = BlockTallGrassTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 8; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k));
                    if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos.up(k)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, temp);
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockShortGrassTFC.AGE, plantAge));
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.CREEPING)
        {
            BlockCreepingPlantTFC plantBlock = BlockCreepingPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state) &&
                    !BlocksTFC.isSand(worldIn.getBlockState(blockpos.down())))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockCreepingPlantTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.STANDARD)
        {
            BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.REED)
        {
            BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    worldIn.getBlockState(blockpos.down()).getBlock().canSustainPlant(state, worldIn, blockpos.down(), EnumFacing.UP, plantBlock))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.TALL_REED)
        {
            BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k));
                    if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos.up(k)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, temp);
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.DESERT)
        {
            BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 128; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.DESERT_TALL_PLANT)
        {
            BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k));
                    if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos.up(k)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, temp);
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.DRY)
        {
            BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 128; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH) &&
                    plantBlock.canBlockStay(worldIn, blockpos, state))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.DRY_TALL_PLANT)
        {
            BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k));
                    if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos.up(k)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, temp);
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.TALL_PLANT)
        {
            BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 32; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k));
                    if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos.up(k)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, temp);
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.WATER || plant.getPlantType() == Plant.PlantType.WATER_SEA)
        {
            BlockWaterPlantTFC plantBlock = BlockWaterPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();
            IBlockState water = plant.getWaterType();

            if ((water == SALT_WATER && !BlocksTFC.isSaltWater(worldIn.getBlockState(position.add(0, -1, 0)))) || !worldIn.isAirBlock(position))
                return false;
            if ((water == FRESH_WATER && !BlocksTFC.isFreshWater(worldIn.getBlockState(position.add(0, -1, 0)))) || !worldIn.isAirBlock(position))
                return false;

            int depth = plant.getValidWaterDepth(worldIn, position, water);
            if (depth == -1) return false;
            BlockPos blockpos = position.add(0, -depth + 1, 0);
            float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

            if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                plantBlock.canPlaceBlockAt(worldIn, blockpos))
            {
                int plantAge = plant.getAgeForWorldgen(rand, temp);
                setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockWaterPlantTFC.AGE, plantAge));
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER || plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER_SEA)
        {
            BlockEmergentTallWaterPlantTFC plantBlock = BlockEmergentTallWaterPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();
            IBlockState water = plant.getWaterType();

            if ((water == SALT_WATER && !BlocksTFC.isSaltWater(worldIn.getBlockState(position.add(0, -1, 0)))) || !worldIn.isAirBlock(position))
                return false;
            if ((water == FRESH_WATER && !BlocksTFC.isFreshWater(worldIn.getBlockState(position.add(0, -1, 0)))) || !worldIn.isAirBlock(position))
                return false;

            for (int i = 0; i < 16; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), 0, rand.nextInt(8) - rand.nextInt(8));

                int depth = plant.getValidWaterDepth(worldIn, blockpos, water);
                if (depth == -1) return false;
                blockpos = blockpos.add(0, -depth + 1, 0);

                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);
                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockEmergentTallWaterPlantTFC.AGE, plantAge));
                    if (rand.nextInt(15) < plantAge && plantBlock.canGrow(worldIn, blockpos, state, worldIn.isRemote))
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(), state);
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.TALL_WATER || plant.getPlantType() == Plant.PlantType.TALL_WATER_SEA)
        {
            BlockTallWaterPlantTFC plantBlock = BlockTallWaterPlantTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();
            IBlockState water = plant.getWaterType();

            if ((water == SALT_WATER && !BlocksTFC.isSaltWater(worldIn.getBlockState(position.add(0, -1, 0)))) || !worldIn.isAirBlock(position))
                return false;
            if ((water == FRESH_WATER && !BlocksTFC.isFreshWater(worldIn.getBlockState(position.add(0, -1, 0)))) || !worldIn.isAirBlock(position))
                return false;


            int depth = plant.getValidWaterDepth(worldIn, position, water);
            if (depth == -1) return false;
            BlockPos blockpos = position.add(0, -depth + 1, 0);

            int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

            for (int k = 0; k < j; ++k)
            {
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k));
                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos.up(k)) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos.up(k)))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    if (k > 0 && !worldIn.isAirBlock(blockpos.up(k))) return true;
                    setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.FLOATING)
        {
            BlockFloatingWaterTFC plantBlock = BlockFloatingWaterTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();
            IBlockState water = plant.getWaterType();

            for (int i = 0; i < 8; ++i)
            {
                final BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), 0, rand.nextInt(8) - rand.nextInt(8));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos) &&
                    plant.isValidFloatingWaterDepth(worldIn, blockpos, water))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockFloatingWaterTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.FLOATING_SEA)
        {
            BlockFloatingWaterTFC plantBlock = BlockFloatingWaterTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();
            IBlockState water = plant.getWaterType();

            for (int i = 0; i < 128; ++i)
            {
                final BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), 0, rand.nextInt(8) - rand.nextInt(8));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.isAirBlock(blockpos) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos) &&
                    plant.isValidFloatingWaterDepth(worldIn, blockpos, water))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockFloatingWaterTFC.AGE, plantAge));
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.CACTUS)
        {
            BlockCactusTFC plantBlock = BlockCactusTFC.get(plant);
            IBlockState state = plantBlock.getDefaultState();

            for (int i = 0; i < 10; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos.up(k));
                    if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos.up(k)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                        worldIn.isAirBlock(blockpos.up(k)) &&
                        plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, temp);
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockCactusTFC.AGE, plantAge));
                    }
                }
            }
        }
        else if (plant.getPlantType() == Plant.PlantType.EPIPHYTE)
        {
            BlockEpiphyteTFC plantBlock = BlockEpiphyteTFC.get(plant);

            for (int i = 0; i < 128; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(16), rand.nextInt(8) - rand.nextInt(8));
                float temp = ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, blockpos);

                if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockpos) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, temp);
                    setBlockAndNotifyAdequately(worldIn, blockpos, plantBlock.getStateForWorldGen(worldIn, blockpos).withProperty(BlockEpiphyteTFC.AGE, plantAge));
                }
            }
        }

        return true;
    }
}