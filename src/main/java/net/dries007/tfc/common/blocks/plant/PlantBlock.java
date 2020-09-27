/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;

public abstract class PlantBlock extends TFCBushBlock
{
    protected static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    /*
     * Time of day, used for rendering plants that bloom at different times
     * 0 = midnight-dawn
     * 1 = dawn-noon
     * 2 = noon-dusk
     * 3 = dusk-midnight
     */
    protected final static IntegerProperty DAYPERIOD = IntegerProperty.create("dayperiod", 0, 3);
    protected static final VoxelShape PLANT_SHAPE = makeCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected IntegerProperty stage;

    public PlantBlock(Properties properties)
    {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return PLANT_SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        // Update state
        double growthChance = TFCConfig.SERVER.plantGrowthRate.get();
        if (world.isRainingAt(pos))
        {
            growthChance *= 5;
        }
        if (RANDOM.nextDouble() < growthChance && ForgeHooks.onCropsGrowPre(world, pos.up(), state, true))
        {
            state = state.with(AGE, Math.min(state.get(AGE) + 1, 3));
        }
        world.setBlockState(pos, state.with(stage, getMonthStage(Calendars.SERVER.getCalendarMonthOfYear())).with(DAYPERIOD, getDayTime()));
    }

    public abstract float getSpeedFactor();

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        stage = IntegerProperty.create("stage", 0, Math.max(1, getMaxStage()));
        builder.add(stage, DAYPERIOD, AGE);
    }

    public abstract int getMaxStage();

    public abstract int getMonthStage(Month month);

    protected int getDayTime()
    {
        return ICalendar.getHourOfDay(Calendars.SERVER.getCalendarTicks()) / (ICalendar.HOURS_IN_DAY / 4);
    }
}
