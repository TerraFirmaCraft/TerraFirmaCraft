/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.IngotPileBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class IngotPileBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_64;

    private static final VoxelShape[] SHAPES = {
        box(0.25, 0, 0.25, 15.75, 2, 15.75),
        box(0.25, 0, 0.25, 15.75, 4, 15.75),
        box(0.25, 0, 0.25, 15.75, 6, 15.75),
        box(0.25, 0, 0.25, 15.75, 8, 15.75),
        box(0.25, 0, 0.25, 15.75, 10, 15.75),
        box(0.25, 0, 0.25, 15.75, 12, 15.75),
        box(0.25, 0, 0.25, 15.75, 14, 15.75),
        box(0.25, 0, 0.25, 15.75, 16, 15.75)
    };

    public IngotPileBlock(ExtendedProperties properties)
    {
        this(properties, COUNT);
    }

    protected IngotPileBlock(ExtendedProperties properties, IntegerProperty countProperty)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(countProperty, 1));
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
            final int topIngots = topState.getValue(getCountProperty());

            if (level.getBlockEntity(topPos) instanceof IngotPileBlockEntity pile)
            {
                final ItemStack ingot = pile.removeIngot();
                if (!player.isCreative())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, ingot);
                }
            }

            if (topIngots == 1)
            {
                level.removeBlock(topPos, false);
            }
            else
            {
                level.setBlock(topPos, topState.setValue(getCountProperty(), topIngots - 1), Block.UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (!canSurvive(state, level, pos))
        {
            // Neighbor state is not sturdy, so pop off and drop items
            level.destroyBlock(pos, true);
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

        // This spawns destruction particles, but does not actually modify the block
        // Call this before voiding the inventory, so the spawned particles have metals to reference for texture purposes
        playerWillDestroy(level, pos, state, player);

        if (player.isCreative() && canActuallyHarvest && level.getBlockEntity(pos) instanceof IngotPileBlockEntity pile)
        {
            // Void contents when broken in creative, right after spawning particles, so the block breaking won't drop contents
            pile.removeAllIngots(ingot -> {});
        }

        return level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (level.getBlockEntity(pos) instanceof IngotPileBlockEntity pile && newState.getBlock() != this)
        {
            pile.removeAllIngots(ingot -> popResource(level, pos, ingot));
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(getCountProperty()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[(state.getValue(getCountProperty()) - 1) / 8];
    }

    public IntegerProperty getCountProperty()
    {
        return COUNT;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return level.getBlockEntity(pos) instanceof IngotPileBlockEntity pile ? pile.getPickedItemStack() : ItemStack.EMPTY;
    }
}
