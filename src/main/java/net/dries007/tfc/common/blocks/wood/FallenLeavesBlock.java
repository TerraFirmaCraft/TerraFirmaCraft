package net.dries007.tfc.common.blocks.wood;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;

public class FallenLeavesBlock extends GroundcoverBlock
{
    public static final EnumProperty<Season> SEASON = TFCBlockStateProperties.SEASON_NO_SPRING;

    private static final VoxelShape VERY_FLAT = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public FallenLeavesBlock(Properties properties)
    {
        super(properties, VERY_FLAT);

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.EAST).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).setValue(SEASON, Season.SUMMER));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true; // Not for the purposes of leaf decay, but for the purposes of seasonal updates
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        // Adjust the season based on the current time
        Season oldSeason = state.getValue(SEASON);
        Season newSeason = Calendars.SERVER.getCalendarMonthOfYear().getSeason();
        if (newSeason == Season.SPRING)
        {
            newSeason = Season.SUMMER; // Skip spring
        }
        if (oldSeason != newSeason)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(SEASON, newSeason));
        }
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        final Season season = Calendars.get(context.getLevel()).getCalendarMonthOfYear().getSeason();
        final BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(SEASON, season == Season.SPRING ? Season.SUMMER : season);

        if (getFluidProperty().canContain(fluidState.getType()))
        {
            return state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, getFluidProperty(), SEASON);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VERY_FLAT;
    }
}
