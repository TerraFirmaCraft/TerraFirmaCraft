/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rock.IFallableBlock;
import net.dries007.tfc.mixin.accessor.FallingBlockEntityAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;

/**
 * A falling block entity that has a bit more oomph - it destroys blocks underneath it rather than hovering or popping off.
 */
public class TFCFallingBlockEntity extends FallingBlockEntity
{
    public static boolean canFallThrough(BlockGetter world, BlockPos pos)
    {
        return canFallThrough(world, pos, world.getBlockState(pos));
    }

    public static boolean canFallThrough(BlockGetter world, BlockPos pos, BlockState state)
    {
        return !state.isFaceSturdy(world, pos, Direction.UP);
    }

    private final boolean dontSetBlock;
    private boolean failedBreakCheck;

    public TFCFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, Level level)
    {
        super(entityType, level);

        failedBreakCheck = false;
        dontSetBlock = false;
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
            Block block = fallingBlockState.getBlock();
            if (time++ == 0)
            {
                // First tick, replace the existing block
                BlockPos blockpos = blockPosition();
                if (block == level.getBlockState(blockpos).getBlock())
                {
                    level.removeBlock(blockpos, false);
                }
                else if (!level.isClientSide)
                {
                    remove(RemovalReason.DISCARDED);
                    return;
                }
            }

            if (!isNoGravity())
            {
                setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            move(MoverType.SELF, getDeltaMovement());

            if (!level.isClientSide)
            {
                BlockPos posAt = blockPosition();
                if (!onGround)
                {
                    failedBreakCheck = false;
                    if ((time > 100 && (posAt.getY() < 1 || posAt.getY() > 256)) || time > 600)
                    {
                        if (dropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
                        {
                            spawnAtLocation(block);
                        }
                        remove(RemovalReason.DISCARDED);
                    }
                }
                else
                {
                    // On ground
                    if (!failedBreakCheck)
                    {
                        if (!level.isEmptyBlock(posAt) && canFallThrough(level, posAt, level.getBlockState(posAt)))
                        {
                            level.destroyBlock(posAt, true);
                            failedBreakCheck = true;
                            return;
                        }
                        else if (!level.isEmptyBlock(posAt.below()) && canFallThrough(level, posAt.below(), level.getBlockState(posAt.below())))
                        {
                            level.destroyBlock(posAt.below(), true);
                            failedBreakCheck = true;
                            return;
                        }
                    }

                    BlockState hitBlockState = level.getBlockState(posAt);
                    setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));

                    if (hitBlockState.getBlock() != Blocks.MOVING_PISTON)
                    {
                        remove(RemovalReason.DISCARDED);
                        if (!dontSetBlock)
                        {
                            if (hitBlockState.canBeReplaced(new DirectionalPlaceContext(this.level, posAt, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) && fallingBlockState.canSurvive(this.level, posAt) && !FallingBlock.isFree(this.level.getBlockState(posAt.below())))
                            {
                                if (fallingBlockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(posAt).getType() == Fluids.WATER)
                                {
                                    // todo: mixin
                                    // ((FallingBlockEntityAccessor) this).accessor$setBlockState(fallingBlockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE));
                                }

                                if (level.setBlockAndUpdate(posAt, fallingBlockState))
                                {
                                    if (block instanceof FallingBlock)
                                    {
                                        ((FallingBlock) block).onLand(this.level, posAt, fallingBlockState, hitBlockState, this);
                                    }

                                    if (Helpers.isBlock(fallingBlockState.getBlock(), TFCTags.Blocks.CAN_LANDSLIDE))
                                    {
                                        level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(posAt));
                                    }

                                    // Sets the tile entity if it exists
                                    if (blockData != null && fallingBlockState.hasBlockEntity())
                                    {
                                        BlockEntity tileEntity = level.getBlockEntity(posAt);
                                        if (tileEntity != null)
                                        {
                                            CompoundTag tileEntityData = tileEntity.saveWithoutMetadata();
                                            for (String dataKey : tileEntityData.getAllKeys())
                                            {
                                                Tag dataElement = tileEntityData.get(dataKey);
                                                if (!"x".equals(dataKey) && !"y".equals(dataKey) && !"z".equals(dataKey) && dataElement != null)
                                                {
                                                    tileEntityData.put(dataKey, dataElement.copy());
                                                }
                                            }
                                            tileEntity.load(tileEntityData);
                                            tileEntity.setChanged();
                                        }
                                    }
                                }
                                else if (dropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
                                {
                                    spawnAtLocation(block);
                                }
                            }
                            else if (dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
                            {
                                spawnAtLocation(block);
                            }
                        }

                        if (block instanceof IFallableBlock)
                        {
                            ((IFallableBlock) block).onceFinishedFalling(this.level, posAt, this);
                        }
                    }
                }
            }

            setDeltaMovement(getDeltaMovement().scale(0.98D));
        }
    }
}