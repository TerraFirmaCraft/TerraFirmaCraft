/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class AbstractRopeBlock extends HorizontalDirectionalBlock implements IFluidLoggable, IForgeBlockExtension
{
    public static void recallRope(LevelAccessor level, BlockPos pos, BlockState state, Player player, Direction dir)
    {
        final List<BlockPos> positions = new ArrayList<>(32);
        if (!isRope(state))
            return;
        // Note: the anchor block itself is intentionally not added here. It is only the seed for the walk - the caller
        // is responsible for converting it back into a spike - so only the rope blocks hanging from it are recalled.
        // Every rope in a line faces back towards its anchor (FACING == dir.getOpposite(), see RopeItem.placeRopes), so
        // we only continue across ropes with that facing. This stops the walk from straying into an unrelated rope line
        // that this one merely points at but is not physically connected to.
        while (true)
        {
            if (state.getBlock() instanceof HangingRopeBlock)
            {
                pos = pos.below();
                state = level.getBlockState(pos);
                if (continuesLine(state, dir))
                {
                    positions.add(pos);
                }
                else
                {
                    break;
                }
            }
            else
            {
                pos = pos.relative(dir);
                state = level.getBlockState(pos);
                if (!continuesLine(state, dir))
                {
                    pos = pos.below();
                    state = level.getBlockState(pos);
                    if (continuesLine(state, dir))
                    {
                        positions.add(pos);
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    positions.add(pos);
                }
            }
        }
        positions.reversed().forEach(ropePos -> {
            level.destroyBlock(ropePos, false);
            if (!player.isCreative())
                ItemHandlerHelper.giveItemToPlayer(player, TFCItems.ROPE.get().getDefaultInstance());
        });
    }

    private static boolean isRope(BlockState state)
    {
        return state.getBlock() instanceof AbstractRopeBlock;
    }

    private static boolean continuesLine(BlockState state, Direction dir)
    {
        return isRope(state) && state.getValue(FACING) == dir.getOpposite();
    }

    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    public static final VoxelShape SHAPE_X = box(7, 0, 0, 9, 2, 16);
    public static final VoxelShape SHAPE_Z = Helpers.rotateShape(Direction.EAST, 7, 0, 0, 9, 2, 16);

    private final ExtendedProperties properties;

    public AbstractRopeBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        registerDefaultState(getStateDefinition().any().setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)));
    }

    protected boolean isBlockBelowSturdy(LevelReader level, BlockPos pos)
    {
        return !level.getBlockState(pos.below()).canBeReplaced();
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, FLUID));
    }

    @Override
    protected MapCodec<HorizontalDirectionalBlock> codec()
    {
        return fakeBlockCodec();
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
