/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;

import javax.annotation.Nonnull;


public abstract class TFCLeavesBlock extends Block
{
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final EnumProperty<Season> SEASON_NO_SPRING = TFCBlockStateProperties.SEASON_NO_SPRING;

    public static TFCLeavesBlock create(Properties properties, int maxDecayDistance)
    {
        final IntegerProperty distanceProperty = getDistanceProperty(maxDecayDistance);
        return new TFCLeavesBlock(properties, maxDecayDistance)
        {
            @Override
            protected IntegerProperty getDistanceProperty()
            {
                return distanceProperty;
            }
        };
    }

    private static IntegerProperty getDistanceProperty(int maxDecayDistance)
    {
        switch (maxDecayDistance)
        {
            case 7:
                return TFCBlockStateProperties.DISTANCE_1_7;
            case 8:
                return TFCBlockStateProperties.DISTANCE_1_8;
            default:
                throw new IllegalArgumentException("No property set for distance: " + maxDecayDistance);
        }
    }

    /* The maximum value of the decay property. */
    private final int maxDecayDistance;

    protected TFCLeavesBlock(Properties properties, int maxDecayDistance)
    {
        super(properties);
        this.maxDecayDistance = maxDecayDistance;

        // Distance is dependent on tree species
        registerDefaultState(stateDefinition.any().setValue(getDistanceProperty(), 1).setValue(PERSISTENT, false).setValue(SEASON_NO_SPRING, Season.SUMMER));
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        int i = getDistance(facingState) + 1;
        if (i != 1 || stateIn.getValue(getDistanceProperty()) != i)
        {
            worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
        }
        return stateIn;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true; // Not for the purposes of leaf decay, but for the purposes of seasonal updates
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 1;
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

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.getValue(PERSISTENT))
        {
            int distance = updateDistance(worldIn, pos);
            if (distance > maxDecayDistance)
            {
                // Send a message, help the dev's figure out which trees need larger leaf decay radii:
                LOGGER.info("Block: {} decayed at distance {}", state.getBlock().getRegistryName(), distance);
                worldIn.removeBlock(pos, false);
            }
            else
            {
                worldIn.setBlock(pos, state.setValue(getDistanceProperty(), distance), 3);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        entityIn.makeStuckInBlock(state, new Vector3d(0.5D, (double)0.4F, 0.5D));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PERSISTENT, SEASON_NO_SPRING, getDistanceProperty());
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Season season = Calendars.SERVER.getCalendarMonthOfYear().getSeason();
        Season newSeason = season == Season.SPRING ? Season.SUMMER : season;
        return super.getStateForPlacement(context).setValue(SEASON_NO_SPRING, newSeason).setValue(PERSISTENT, context.getPlayer() != null);
    }

    /**
     * The reason this is not a constructor parameter is because the super class (Block) will use this directly, and nothing else is initialized in time.
     */
    protected abstract IntegerProperty getDistanceProperty();

    private int updateDistance(IWorld worldIn, BlockPos pos)
    {
        int distance = 1 + maxDecayDistance;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction direction : Direction.values())
        {
            mutablePos.set(pos).move(direction);
            distance = Math.min(distance, getDistance(worldIn.getBlockState(mutablePos)) + 1);
            if (distance == 1)
            {
                break;
            }
        }
        return distance;
    }

    private int getDistance(BlockState neighbor)
    {
        if (BlockTags.LOGS.contains(neighbor.getBlock()))
        {
            return 0;
        }
        else
        {
            // Check against this leaf block only, not any leaves
            return neighbor.getBlock() == this ? neighbor.getValue(getDistanceProperty()) : maxDecayDistance;
        }
    }
}