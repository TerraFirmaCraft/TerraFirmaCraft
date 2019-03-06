/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class BlockDoublePlantTFC extends BlockStackPlantTFC implements IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    protected static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D);
    private static final Map<Plant, EnumMap<Plant.PlantType, BlockDoublePlantTFC>> TABLE = new HashMap<>();

    public static BlockDoublePlantTFC get(Plant plant, Plant.PlantType type)
    {
        return BlockDoublePlantTFC.TABLE.get(plant).get(type);
    }

    public final Plant plant;
    public final Plant.PlantType type;

    public BlockDoublePlantTFC(Plant plant, Plant.PlantType type)
    {
        super(plant, type);
        if (!TABLE.containsKey(plant))
            TABLE.put(plant, new EnumMap<>(Plant.PlantType.class));
        TABLE.get(plant).put(type, this);

        this.plant = plant;
        this.type = type;
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, EnumBlockPart.SINGLE));
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        int i;
        for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {}
        return i < 2 && worldIn.isAirBlock(pos.up()) && canBlockStay(worldIn, pos.up(), state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos.up(), this.getDefaultState());
        IBlockState iblockstate = state.withProperty(AGE, 0).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, state.getValue(PART));
        worldIn.setBlockState(pos, iblockstate);
        iblockstate.neighborChanged(worldIn, pos.up(), this, pos);
    }

    public boolean canShrink(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return worldIn.getBlockState(pos.down()).getBlock() == this;
    }

    public void shrink(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockToAir(pos);
        worldIn.getBlockState(pos).neighborChanged(worldIn, pos.down(), this, pos);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos, worldIn.getBlockState(pos)) : false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            if (getPlantPart(worldIn, pos) == EnumBlockPart.LOWER)
            {
                worldIn.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
            }
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            if (getPlantPart(worldIn, pos) != EnumBlockPart.UPPER)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
            }
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (getPlantPart(worldIn, pos) == EnumBlockPart.LOWER)
        {
            worldIn.setBlockToAir(pos.up());
        }
        if (getPlantPart(worldIn, pos) == EnumBlockPart.UPPER)
        {
            worldIn.setBlockToAir(pos);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(TIME, state.getValue(TIME)).withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, getPlantPart(worldIn, pos));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE, GROWTHSTAGE, PART, TIME});
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return PLANT_AABB.offset(state.getOffset(source, pos));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, meta).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id());
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;
        int currentStage = state.getValue(GROWTHSTAGE);
        int expectedStage = CalenderTFC.getMonthOfYear().id();
        int currentTime = state.getValue(TIME);
        int expectedTime = getCurrentTime(worldIn);

        if (currentTime != expectedTime)
        {
            worldIn.setBlockState(pos, state.withProperty(TIME, expectedTime).withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, currentStage).withProperty(PART, state.getValue(PART)));
        }
        if (currentStage != expectedStage && random.nextDouble() < 0.5)
        {
            worldIn.setBlockState(pos, state.withProperty(TIME, expectedTime).withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, expectedStage).withProperty(PART, state.getValue(PART)));
        }
        this.updateTick(worldIn, pos, state, random);
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return EnumOffsetType.XYZ;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if ((ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) < 15 || !plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up()))) && canShrink(worldIn, pos, state, worldIn.isRemote))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
            {
                if (j == 0)
                {
                    shrink(worldIn, rand, pos, state);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j - 1).withProperty(GROWTHSTAGE, state.getValue(GROWTHSTAGE)).withProperty(PART, state.getValue(PART)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
        else if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > 20 && plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up())) && canGrow(worldIn, pos, state, worldIn.isRemote))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos.up(), state, true))
            {
                if (j == 15)
                {
                    grow(worldIn, rand, pos, state);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(GROWTHSTAGE, state.getValue(GROWTHSTAGE)).withProperty(PART, state.getValue(PART)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }

        if (!canBlockStay(worldIn, pos, state))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());

        if (worldIn.getBlockState(pos.down(2)).getBlock() == this) return false;
        if (state.getBlock() == this)
        {
            return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this) && plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos), ChunkDataTFC.getRainfall(worldIn, pos));
        }
        return this.canSustainBush(soil);
    }
}