/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rock.IFallableBlock;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.mixin.accessor.FallingBlockEntityAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tracker.WorldTracker;

/**
 * A falling block entity that has a bit more oomph - it destroys blocks underneath it rather than hovering or popping off.
 */
public class TFCFallingBlockEntity extends FallingBlockEntity
{
    public static boolean canFallThrough(BlockGetter world, BlockPos pos, BlockState state)
    {
        return !state.isFaceSturdy(world, pos, Direction.UP);
    }

    /**
     * Can the existing block at {@code pos} fall through the block in the direction {@code fallingDirection}.
     *
     * @param level            The world
     * @param pos              The position of the existing block that might fall
     * @param fallingDirection The direction that the existing block might fall in
     * @return {@code true} if the block at {@code pos} can fall through the block in the direction {@code fallingDirection}.
     */
    public static boolean canFallInDirection(BlockGetter level, BlockPos pos, Direction fallingDirection)
    {
        final BlockPos fallThroughPos = pos.relative(fallingDirection);
        return canFallThrough(level, fallThroughPos, level.getBlockState(fallThroughPos), fallingDirection, level.getBlockState(pos));
    }

    public static boolean canFallThrough(BlockGetter level, BlockPos pos, Direction fallingDirection)
    {
        final BlockState state = level.getBlockState(pos);
        return canFallThrough(level, pos, state, fallingDirection, state);
    }

    public static boolean canFallThrough(BlockGetter level, BlockPos pos, Direction fallingDirection, BlockState fallingState)
    {
        return canFallThrough(level, pos, level.getBlockState(pos), fallingDirection, fallingState);
    }

    /**
     * Can the falling block fall through (effectively destroying) a specific block
     *
     * @param level            The world
     * @param pos              The position of the block in world that we want to fall through
     * @param state            {@code level.getBlockState(pos)}
     * @param fallingDirection The direction of the fall. For most falls this will be {@link Direction#DOWN}, however for landslides, this may be a horizontal direction, indicating we want to move into the block from the side.
     * @param fallingState     The state of the falling block. This is used in order to calculate toughness, if the falling block can break the existing block.
     * @return {@code true} if the falling block can fall through the existing block.
     */
    public static boolean canFallThrough(BlockGetter level, BlockPos pos, BlockState state, Direction fallingDirection, BlockState fallingState)
    {
        return !state.isFaceSturdy(level, pos, fallingDirection.getOpposite()) // Must be non-sturdy in the direction opposed to the fall
            && getBlockToughness(fallingState) >= getBlockToughness(state) // Must be of an equal or greater toughness
            && state.getDestroySpeed(level, pos) > -1f && !(state.getBlock() == Blocks.STRUCTURE_VOID); // Don't break end portal frames or structure voids
    }

    public static int getBlockToughness(BlockState state)
    {
        if (state.getBlock() == Blocks.BEDROCK)
        {
            return 4; // Fake value, useful for simulating really hard toughness checks.
        }
        if (Helpers.isBlock(state, TFCTags.Blocks.TOUGHNESS_3))
        {
            return 3;
        }
        if (Helpers.isBlock(state, TFCTags.Blocks.TOUGHNESS_2))
        {
            return 2;
        }
        if (Helpers.isBlock(state, TFCTags.Blocks.TOUGHNESS_1))
        {
            return 1;
        }
        return 0;
    }

    private final boolean dontSetBlock;
    private boolean failedBreakCheck;

    public TFCFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, Level level)
    {
        super(entityType, level);

        failedBreakCheck = false;
        dontSetBlock = false;
    }

    public TFCFallingBlockEntity(Level level, double x, double y, double z, BlockState fallingBlockState, float damagePerBlockFallen, int maximumFallDamage)
    {
        this(level, x, y, z, fallingBlockState);
        setHurtsEntities(damagePerBlockFallen, maximumFallDamage);
    }

    public TFCFallingBlockEntity(Level level, double x, double y, double z, BlockState fallingBlockState)
    {
        this(TFCEntities.FALLING_BLOCK.get(), level);
        ((FallingBlockEntityAccessor) this).setBlockState(fallingBlockState);
        blocksBuilding = true;
        setPos(x, y, z);
        setDeltaMovement(Vec3.ZERO);
        xo = x;
        yo = y;
        zo = z;
        setStartPos(blockPosition());
    }

    @Override
    public void tick()
    {
        final BlockState fallingBlockState = getBlockState();
        if (fallingBlockState.isAir())
        {
            remove(RemovalReason.DISCARDED);
        }
        else
        {
            final Block block = fallingBlockState.getBlock();
            if (time++ == 0)
            {
                // First tick, replace the existing block
                final BlockPos pos = blockPosition();
                if (block == level().getBlockState(pos).getBlock())
                {
                    level().removeBlock(pos, false);
                }
                else if (!level().isClientSide)
                {
                    remove(RemovalReason.DISCARDED);
                }
                // If we spawn two falling block entities on the same tick, in adjacent positions, and the one above ticks first, it can cause a situation where the block below gets deleted by the falling block destruction code.
                // This causes the next block entity to disappear. So, we don't do anything on first tick except capture and replace the block.
                return;
            }

            applyGravity();
            move(MoverType.SELF, getDeltaMovement());
            handlePortal();

            if (!level().isClientSide && (this.isAlive() || this.forceTickAfterTeleportToDuplicate))
            {
                final BlockPos posAt = blockPosition();
                if (!onGround())
                {
                    failedBreakCheck = false;
                    if ((time > 100 && (posAt.getY() < 1 || posAt.getY() > 256)) || time > 600)
                    {
                        attemptToDropAsItem(fallingBlockState);
                        remove(RemovalReason.DISCARDED);
                    }
                }
                else
                {
                    // On ground
                    if (!failedBreakCheck)
                    {
                        if (!FluidHelpers.isAirOrEmptyFluid(level().getBlockState(posAt)) && canFallThrough(level(), posAt, Direction.DOWN, fallingBlockState))
                        {
                            level().destroyBlock(posAt, true);
                            failedBreakCheck = true;
                            return;
                        }
                        else if (!FluidHelpers.isAirOrEmptyFluid(level().getBlockState(posAt.below())) && canFallThrough(level(), posAt.below(), Direction.DOWN, fallingBlockState))
                        {
                            level().destroyBlock(posAt.below(), true);
                            failedBreakCheck = true;
                            return;
                        }
                    }

                    BlockState hitBlockState = level().getBlockState(posAt);
                    setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));

                    if (hitBlockState.getBlock() != Blocks.MOVING_PISTON)
                    {
                        remove(RemovalReason.DISCARDED);
                        if (!dontSetBlock)
                        {
                            // Attempt to set the block, first by replacing the target block.
                            if (canPlaceAt(hitBlockState, posAt, fallingBlockState, fallingBlockState))
                            {
                                placeAsBlockOrDropAsItem(hitBlockState, posAt, fallingBlockState);
                            }
                            else
                            {
                                // Second check: try and place the block one block above from it's current position
                                // This is to handle blocks such as soul sand or mud, which it will attempt to fall into (because it has a < a block collision shape), but then needs to pretend to place above the block (since they support falling blocks).
                                // Note that the second time we do this, we have to use bedrock as the toughness check - because we only want to place if we can't fall, and can't fall includes checks against toughness - not just against sturdy ground.
                                final BlockPos posAbove = posAt.above();
                                final BlockState hitAboveBlockState = level().getBlockState(posAbove);
                                if (canPlaceAt(hitAboveBlockState, posAbove, fallingBlockState, Blocks.BEDROCK.defaultBlockState()))
                                {
                                    placeAsBlockOrDropAsItem(hitAboveBlockState, posAbove, fallingBlockState);
                                }
                                else
                                {
                                    // Cannot find a location where can survive, and breaking checks have failed.
                                    attemptToDropAsItem(fallingBlockState);
                                }
                            }
                        }

                        if (block instanceof IFallableBlock fallingBlock)
                        {
                            fallingBlock.onceFinishedFalling(this.level(), posAt, this);
                        }
                    }
                }
            }
            setDeltaMovement(getDeltaMovement().scale(0.98D));
        }
    }

    private boolean canPlaceAt(BlockState hitBlockState, BlockPos posAt, BlockState fallingBlockState, BlockState toughnessBlockState)
    {
        final BlockPos below = posAt.below();
        return hitBlockState.canBeReplaced(new DirectionalPlaceContext(this.level(), posAt, Direction.DOWN, ItemStack.EMPTY, Direction.UP))
            && fallingBlockState.canSurvive(this.level(), posAt)
            && !canFallThrough(this.level(), below, Direction.DOWN, toughnessBlockState);
    }

    private void placeAsBlockOrDropAsItem(BlockState hitBlockState, BlockPos posAt, BlockState fallingBlockState)
    {
        if (level().setBlockAndUpdate(posAt, fallingBlockState))
        {
            afterPlacementAsBlock(hitBlockState, posAt, fallingBlockState);
        }
        else
        {
            attemptToDropAsItem(fallingBlockState);
        }
    }

    private void afterPlacementAsBlock(BlockState hitBlockState, BlockPos posAt, BlockState fallingBlockState)
    {
        if (fallingBlockState.getBlock() instanceof FallingBlock fallingBlock)
        {
            fallingBlock.onLand(this.level(), posAt, fallingBlockState, hitBlockState, this);
        }

        if (Helpers.isBlock(fallingBlockState.getBlock(), TFCTags.Blocks.CAN_LANDSLIDE))
        {
            WorldTracker.get(level()).addLandslidePos(posAt);
        }

        // Sets the tile entity if it exists
        if (blockData != null && fallingBlockState.hasBlockEntity())
        {
            final BlockEntity blockEntity = level().getBlockEntity(posAt);
            if (blockEntity != null)
            {
                final CompoundTag blockEntityData = blockEntity.saveWithoutMetadata(level().registryAccess());
                for (String key : blockEntityData.getAllKeys())
                {
                    blockEntityData.put(key, blockData.get(key).copy());
                }
                blockEntity.loadWithComponents(blockEntityData, level().registryAccess());
                blockEntity.setChanged();
            }
        }
    }

    private void attemptToDropAsItem(BlockState fallingBlockState)
    {
        if (dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && level() instanceof ServerLevel server)
        {
            Helpers.dropWithContext(server, fallingBlockState, blockPosition(), p -> {}, true);
        }
    }
}