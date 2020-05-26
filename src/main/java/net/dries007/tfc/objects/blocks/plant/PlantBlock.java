/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

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

public abstract class PlantBlock extends TFCBushBlock
{
    // How many random ticks (on average, this is random after all) is needed for plants to grow one age
    protected static final int GROWTH_PERIOD = 1000; // todo config?
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
        if (RANDOM.nextInt(GROWTH_PERIOD) == 0)
        {
            state = state.with(AGE, Math.min(state.get(AGE) + 1, 3));
        }
        int dayTime = (int) (world.getDayTime() / 6000);
        world.setBlockState(pos, state.with(stage, getMonthStage()).with(DAYPERIOD, dayTime));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        stage = IntegerProperty.create("stage", 0, Math.max(1, getPlant().getMaxStage()));
        builder.add(stage, DAYPERIOD, AGE);
    }

    /**
     * Only way to do without having to create a class for each plant with different number of stages
     * Or ATing into {@link Block}'s stateContainer to make it not final
     */
    public abstract Plant getPlant();

    protected int getMonthStage()
    {
        // todo return the stage this plant should be in the current month, using calendar
        return 0;
    }

    protected int getDayTime()
    {
        // todo return the day time period using calendar
        return 0;
    }
}
