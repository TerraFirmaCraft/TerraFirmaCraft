package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;

public class TFCVineBlock extends VineBlock
{
    public static final EnumProperty<Season> SEASON_NO_SPRING = TFCBlockStateProperties.SEASON_NO_SPRING;

    public TFCVineBlock(AbstractBlock.Properties properties)
    {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(SEASON_NO_SPRING, Season.SUMMER));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        super.randomTick(state, worldIn, pos, random);
        // Adjust the season based on the current time
        Season oldSeason = state.getValue(SEASON_NO_SPRING);
        Season newSeason = getSeasonForState();
        if (oldSeason != newSeason)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(SEASON_NO_SPRING, newSeason));
        }
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return super.getStateForPlacement(context).setValue(SEASON_NO_SPRING, getSeasonForState());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SEASON_NO_SPRING));
    }

    private Season getSeasonForState()
    {
        Season season = Calendars.SERVER.getCalendarMonthOfYear().getSeason();
        return season == Season.SPRING ? Season.SUMMER : season;
    }
}
