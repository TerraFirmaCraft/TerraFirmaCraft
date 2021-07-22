/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class PlantBlock extends TFCBushBlock
{
    public static final IntegerProperty AGE = TFCBlockStateProperties.AGE_3;

    protected static final VoxelShape PLANT_SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public static PlantBlock create(IPlant plant, Properties properties)
    {
        return new PlantBlock(properties)
        {

            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected PlantBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(getPlant().getStageProperty(), 0).setValue(AGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (random.nextDouble() < TFCConfig.SERVER.plantGrowthChance.get())
        {
            state = state.setValue(AGE, Math.min(state.getValue(AGE) + 1, 3));
        }
        world.setBlockAndUpdate(pos, updateStateWithCurrentMonth(state));
    }

    /**
     * Gets the plant metadata for this block.
     *
     * The stage property is isolated and referenced via this as it is needed in the {@link net.minecraft.block.Block} constructor - which builds the state container, and requires all property references to be computed in {@link Block#createBlockStateDefinition(StateContainer.Builder)}.
     *
     * See the various {@link PlantBlock#create(IPlant, Properties)} methods and subclass versions for how to use.
     */
    public abstract IPlant getPlant();

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return updateStateWithCurrentMonth(defaultBlockState());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(getPlant().getStageProperty(), AGE);
    }

    protected BlockState updateStateWithCurrentMonth(BlockState stateIn)
    {
        return stateIn.setValue(getPlant().getStageProperty(), getPlant().stageFor(Calendars.SERVER.getCalendarMonthOfYear()));
    }
}
