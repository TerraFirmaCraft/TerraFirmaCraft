package net.dries007.tfc.common.blocks.wood;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

public class FallenLeavesBlock extends GroundcoverBlock
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final EnumProperty<Season> SEASON_NO_SPRING = TFCBlockStateProperties.SEASON_NO_SPRING;

    private static final VoxelShape VERY_FLAT = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public FallenLeavesBlock()
    {
        super(Properties.of(Material.GRASS).strength(0.05F, 0.0F).noOcclusion().sound(SoundType.CROP));

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.EAST).setValue(WATERLOGGED, false).setValue(SEASON_NO_SPRING, Season.SUMMER));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED, SEASON_NO_SPRING);
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
        Season oldSeason = state.getValue(SEASON_NO_SPRING);
        Season newSeason = Calendars.SERVER.getCalendarMonthOfYear().getSeason();
        if (newSeason == Season.SPRING)
        {
            newSeason = Season.SUMMER; // Skip spring
        }
        if (oldSeason != newSeason)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(SEASON_NO_SPRING, newSeason));
        }
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.is(FluidTags.WATER) && fluidstate.isSource();
        Season season = Calendars.get(context.getLevel()).getCalendarMonthOfYear().getSeason();
        Season newSeason = season == Season.SPRING ? Season.SUMMER : season;
        return super.getStateForPlacement(context).setValue(WATERLOGGED, flag).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(SEASON_NO_SPRING, newSeason);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VERY_FLAT;
    }
}
