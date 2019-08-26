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
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

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
        if (plant.getIsSwampPlant() && (/*!ClimateTFC.isSwamp(worldIn, position) ||*/ !BiomeDictionary.hasType(worldIn.getBiome(position), BiomeDictionary.Type.SWAMP)))
            return false;

        switch (plant.getPlantType())
        {
            case MUSHROOM:
            {
                BlockMushroomTFC plantBlock = BlockMushroomTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 16; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        plantBlock.canPlaceBlockAt(worldIn, blockpos))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case SHORT_GRASS:
            {
                BlockShortGrassTFC plantBlock = BlockShortGrassTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 4; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidGrowthTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        plantBlock.canBlockStay(worldIn, blockpos, state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockShortGrassTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case TALL_GRASS:
            {
                BlockTallGrassTFC plantBlock = BlockTallGrassTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 16; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    int j = 1 + rand.nextInt(plant.getMaxHeight());

                    for (int k = 0; k < j; ++k)
                    {
                        if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                            plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                            worldIn.isAirBlock(blockpos.up(k)) &&
                            plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                        {
                            int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                            setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockShortGrassTFC.AGE, plantAge));
                        }
                    }
                }
                break;
            }
            case CREEPING:
            {
                BlockCreepingPlantTFC plantBlock = BlockCreepingPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 16; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        plantBlock.canBlockStay(worldIn, blockpos, state) &&
                        !BlocksTFC.isSand(worldIn.getBlockState(blockpos.down())))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockCreepingPlantTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case HANGING:
            {
                BlockHangingPlantTFC plantBlock = BlockHangingPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 4; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(16), rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        plantBlock.canBlockStay(worldIn, blockpos, state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockHangingPlantTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case REED:
            case REED_SEA:
            {
                BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 16; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        worldIn.getBlockState(blockpos.down()).getBlock().canSustainPlant(state, worldIn, blockpos.down(), EnumFacing.UP, plantBlock))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case TALL_REED:
            case TALL_REED_SEA:
            {
                BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 16; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    int j = 1 + rand.nextInt(plant.getMaxHeight());

                    for (int k = 0; k < j; ++k)
                    {
                        if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                            plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                            worldIn.isAirBlock(blockpos.up(k)) &&
                            plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                        {
                            int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                            setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                        }
                    }
                }
                break;
            }
            case DESERT:
            {
                BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position); ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH) &&
                        plantBlock.canBlockStay(worldIn, blockpos, state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case DESERT_TALL_PLANT:
            {
                BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position); ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    int j = 1 + rand.nextInt(plant.getMaxHeight());

                    for (int k = 0; k < j; ++k)
                    {
                        if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                            plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                            worldIn.isAirBlock(blockpos.up(k)) &&
                            plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                        {
                            int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                            setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                        }
                    }
                }
                break;
            }
            case DRY:
            {
                BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position); ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        !BiomeDictionary.hasType(worldIn.getBiome(blockpos), BiomeDictionary.Type.BEACH) &&
                        plantBlock.canBlockStay(worldIn, blockpos, state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case DRY_TALL_PLANT:
            {
                BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position); ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    int j = 1 + rand.nextInt(plant.getMaxHeight());

                    for (int k = 0; k < j; ++k)
                    {
                        if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                            plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                            worldIn.isAirBlock(blockpos.up(k)) &&
                            plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                        {
                            int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                            setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                        }
                    }
                }
                break;
            }
            case TALL_PLANT:
            {
                BlockTallPlantTFC plantBlock = BlockTallPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 16; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    int j = 1 + rand.nextInt(plant.getMaxHeight());

                    for (int k = 0; k < j; ++k)
                    {
                        if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                            plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                            worldIn.isAirBlock(blockpos.up(k)) &&
                            plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                        {
                            int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                            setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                        }
                    }
                }
                break;
            }
            case WATER:
            case WATER_SEA:
            {
                BlockWaterPlantTFC plantBlock = BlockWaterPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();
                IBlockState water = plant.getWaterType();

                int depth = plant.getValidWaterDepth(worldIn, position, water);
                if (depth == -1) return false;

                BlockPos blockpos = position.add(0, -depth + 1, 0);

                if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockWaterPlantTFC.AGE, plantAge));
                }
                break;
            }
            case EMERGENT_TALL_WATER:
            case EMERGENT_TALL_WATER_SEA:
            {
                BlockEmergentTallWaterPlantTFC plantBlock = BlockEmergentTallWaterPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();
                IBlockState water = plant.getWaterType();

                int depth = plant.getValidWaterDepth(worldIn, position, water);
                if (depth == -1) return false;
                BlockPos blockpos = position.add(0, -depth + 1, 0);

                if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockEmergentTallWaterPlantTFC.AGE, plantAge));
                    if (rand.nextInt(3) < plantAge && plantBlock.canGrow(worldIn, blockpos, state, worldIn.isRemote))
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(), state);
                }
                break;
            }
            case TALL_WATER:
            case TALL_WATER_SEA:
            {
                BlockTallWaterPlantTFC plantBlock = BlockTallWaterPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();
                IBlockState water = plant.getWaterType();

                int depth = plant.getValidWaterDepth(worldIn, position, water);
                if (depth == -1) return false;
                BlockPos blockpos = position.add(0, -depth + 1, 0);

                if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                    plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                    plantBlock.canPlaceBlockAt(worldIn, blockpos))
                {
                    int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                    setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockTallPlantTFC.AGE, plantAge));
                    if (rand.nextInt(4) < plantAge && plantBlock.canGrow(worldIn, blockpos, state, worldIn.isRemote))
                        setBlockAndNotifyAdequately(worldIn, blockpos.up(), state);
                }
                break;
            }
            case FLOATING:
            {
                BlockFloatingWaterTFC plantBlock = BlockFloatingWaterTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();
                IBlockState water = plant.getWaterType();

                for (int i = 0; i < 8; ++i)
                {
                    final BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), 0, rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        plantBlock.canPlaceBlockAt(worldIn, blockpos) &&
                        plant.isValidFloatingWaterDepth(worldIn, blockpos, water))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockFloatingWaterTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case FLOATING_SEA:
            {
                BlockFloatingWaterTFC plantBlock = BlockFloatingWaterTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();
                IBlockState water = plant.getWaterType();

                for (int i = 0; i < 128; ++i)
                {
                    final BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), 0, rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        plantBlock.canPlaceBlockAt(worldIn, blockpos) &&
                        plant.isValidFloatingWaterDepth(worldIn, blockpos, water))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockFloatingWaterTFC.AGE, plantAge));
                    }
                }
                break;
            }
            case CACTUS:
            {
                BlockCactusTFC plantBlock = BlockCactusTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 8; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    int j = 1 + rand.nextInt(plant.getMaxHeight());

                    for (int k = 0; k < j; ++k)
                    {
                        if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                            plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos.up(k))) &&
                            worldIn.isAirBlock(blockpos.up(k)) &&
                            plantBlock.canBlockStay(worldIn, blockpos.up(k), state))
                        {
                            int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                            setBlockAndNotifyAdequately(worldIn, blockpos.up(k), state.withProperty(BlockCactusTFC.AGE, plantAge));
                        }
                    }
                }
                break;
            }
            case EPIPHYTE:
            {
                BlockEpiphyteTFC plantBlock = BlockEpiphyteTFC.get(plant);

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 4; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(16), rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos) &&
                        plantBlock.canPlaceBlockAt(worldIn, blockpos))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, plantBlock.getStateForWorldGen(worldIn, blockpos).withProperty(BlockEpiphyteTFC.AGE, plantAge));
                    }
                }
                break;
            }
            default:
            {
                BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
                IBlockState state = plantBlock.getDefaultState();

                for (int i = 0; i < ChunkDataTFC.getRainfall(worldIn, position) / 16; ++i)
                {
                    BlockPos blockpos = position.add(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(7) - rand.nextInt(7));

                    if (plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, blockpos)) &&
                        plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, blockpos)) &&
                        worldIn.isAirBlock(blockpos) &&
                        plantBlock.canBlockStay(worldIn, blockpos, state))
                    {
                        int plantAge = plant.getAgeForWorldgen(rand, ClimateTFC.getActualTemp(worldIn, blockpos));
                        setBlockAndNotifyAdequately(worldIn, blockpos, state.withProperty(BlockPlantTFC.AGE, plantAge));
                    }
                }
            }
        }
        return true;
    }
}
