/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class IngotPileBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_64;

    private static final VoxelShape[] SHAPES = {
        box(0, 0, 0, 16, 2, 16),
        box(0, 0, 0, 16, 4, 16),
        box(0, 0, 0, 16, 6, 16),
        box(0, 0, 0, 16, 8, 16),
        box(0, 0, 0, 16, 10, 16),
        box(0, 0, 0, 16, 12, 16),
        box(0, 0, 0, 16, 14, 16),
        Shapes.block()
    };

    public IngotPileBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(COUNT, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos)
    {
        if (direction == Direction.DOWN && !neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite()) && !Helpers.isBlock(neighborState, this))
        {
            level.scheduleTick(currentPos, this, 1);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!player.isShiftKeyDown())
        {
            // Attempt to remove from the ingot pile, or one above
            // First, climb up the current stack until we locate the top ingot pile
            BlockPos topPos = pos;
            while (Helpers.isBlock(level.getBlockState(topPos.above()), this))
            {
                topPos = topPos.above();
            }

            // topPos is an ingot pile
            final BlockState topState = level.getBlockState(topPos);
            final int topIngots = topState.getValue(COUNT);

            level.getBlockEntity(topPos, TFCBlockEntities.INGOT_PILE.get()).ifPresent(pile -> ItemHandlerHelper.giveItemToPlayer(player, pile.removeIngot()));

            if (topIngots == 1)
            {
                level.removeBlock(topPos, false);
            }
            else
            {
                level.setBlock(topPos, topState.setValue(COUNT, topIngots - 1), Block.UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        if (!canSurvive(state, level, pos))
        {
            // Neighbor state is not sturdy, so pop off and drop items
            level.getBlockEntity(pos, TFCBlockEntities.INGOT_PILE.get()).ifPresent(pile -> {
                for (ItemStack ingot : pile.removeAllIngots())
                {
                    popResource(level, pos, ingot);
                }
            });
            level.destroyBlock(pos, false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos adjacentPos = pos.below();
        final BlockState adjacentState = level.getBlockState(adjacentPos);
        return adjacentState.isFaceSturdy(level, adjacentPos, Direction.UP) || Helpers.isBlock(adjacentState, this);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        final boolean canActuallyHarvest = state.canHarvestBlock(level, pos, player);
        if (!player.isCreative() && canActuallyHarvest)
        {
            level.getBlockEntity(pos, TFCBlockEntities.INGOT_PILE.get()).ifPresent(pile -> {
                for (ItemStack ingot : pile.removeAllIngots())
                {
                    popResource(level, pos, ingot);
                }
            });
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(COUNT));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[(state.getValue(COUNT) - 1) / 8];
    }
}
