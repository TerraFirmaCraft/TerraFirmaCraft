/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.util.ITallPlant;
import net.dries007.tfc.world.classic.CalendarTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.world.classic.ChunkGenTFC.SALT_WATER;

@ParametersAreNonnullByDefault
public class BlockTallWaterPlantTFC extends BlockWaterPlantTFC implements IGrowable, ITallPlant
{
    private static final PropertyEnum<EnumBlockPart> PART = PropertyEnum.create("part", EnumBlockPart.class);
    private static final Map<Plant, BlockTallWaterPlantTFC> MAP = new HashMap<>();

    public static BlockTallWaterPlantTFC get(Plant plant)
    {
        return BlockTallWaterPlantTFC.MAP.get(plant);
    }

    public BlockTallWaterPlantTFC(Plant plant)
    {
        super(plant);
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        IBlockState water = plant.getWaterType();
        int i;
        //noinspection StatementWithEmptyBody
        for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) ;
        if (water == SALT_WATER)
            return i < plant.getMaxHeight() && BlocksTFC.isSaltWater(worldIn.getBlockState(pos.up())) && canBlockStay(worldIn, pos.up(), state);
        else
            return i < plant.getMaxHeight() && BlocksTFC.isFreshWater(worldIn.getBlockState(pos.up())) && canBlockStay(worldIn, pos.up(), state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos.up(), this.getDefaultState());
        IBlockState iblockstate = state.withProperty(AGE, 0).withProperty(GROWTHSTAGE, plant.getStages()[CalendarTFC.getMonthOfYear().id()]).withProperty(PART, getPlantPart(worldIn, pos));
        worldIn.setBlockState(pos, iblockstate);
        iblockstate.neighborChanged(worldIn, pos.up(), this, pos);
    }

    public void shrink(World worldIn, BlockPos pos)
    {
        worldIn.setBlockState(pos, plant.getWaterType());
        worldIn.getBlockState(pos).neighborChanged(worldIn, pos.down(), this, pos);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
    {
        IBlockState plant = plantable.getPlant(world, pos.offset(direction));

        if (plant.getBlock() == this)
        {
            return true;
        }
        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(DAYPERIOD, getDayPeriod()).withProperty(AGE, meta).withProperty(GROWTHSTAGE, plant.getStages()[CalendarTFC.getMonthOfYear().id()]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, plant.getStages()[CalendarTFC.getMonthOfYear().id()]).withProperty(PART, getPlantPart(worldIn, pos));
    }

    @Override
    @Nonnull
    public Block.EnumOffsetType getOffsetType()
    {
        return EnumOffsetType.XYZ;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if (plant.isValidGrowthTemp(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos)) && plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up())))
        {
            int j = state.getValue(AGE);

            if (rand.nextDouble() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos.up(), state, true))
            {
                if (j == 3 && canGrow(worldIn, pos, state, worldIn.isRemote))
                {
                    grow(worldIn, rand, pos, state);
                }
                else if (j < 3)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(PART, getPlantPart(worldIn, pos)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
        else if (CalendarTFC.getCalendarTime() > Math.multiplyExact(CalendarTFC.TICKS_IN_DAY, CalendarTFC.getDaysInMonth()))
        {
            int j = state.getValue(AGE);

            if (rand.nextDouble() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
            {
                if (j == 0 && canShrink(worldIn, pos))
                {
                    shrink(worldIn, pos);
                }
                else if (j > 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j - 1).withProperty(PART, getPlantPart(worldIn, pos)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }

        checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());

        if (worldIn.getBlockState(pos.down(plant.getMaxHeight())).getBlock() == this) return false;
        if (state.getBlock() == this)
        {
            return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this) && plant.isValidTemp(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos)) && plant.isValidRain(ChunkDataTFC.getRainfall(worldIn, pos));
        }
        return this.canSustainBush(soil);
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return getTallBoundingBax(state.getValue(AGE), state, source, pos);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createPlantBlockState()
    {
        return new BlockStateContainer(this, AGE, GROWTHSTAGE, PART, DAYPERIOD);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, worldIn.getBlockState(pos));
    }

    private boolean canShrink(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getBlock() == this && worldIn.getBlockState(pos.up()).getBlock() != this;
    }
}