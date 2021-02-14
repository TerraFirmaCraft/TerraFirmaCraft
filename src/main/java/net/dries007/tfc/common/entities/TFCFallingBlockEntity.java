/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rock.IFallableBlock;
import net.dries007.tfc.mixin.entity.item.FallingBlockEntityAccessor;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;

/**
 * A falling block entity that has a bit more oomph - it destroys blocks underneath it rather than hovering or popping off.
 */
public class TFCFallingBlockEntity extends FallingBlockEntity
{
    public static boolean canFallThrough(IBlockReader world, BlockPos pos)
    {
        return canFallThrough(world, pos, world.getBlockState(pos));
    }

    public static boolean canFallThrough(IBlockReader world, BlockPos pos, BlockState state)
    {
        return !state.isFaceSturdy(world, pos, Direction.UP);
    }

    private final boolean dontSetBlock;
    private boolean failedBreakCheck;

    public TFCFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world)
    {
        super(entityType, world);

        failedBreakCheck = false;
        dontSetBlock = false;
    }

    public TFCFallingBlockEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState)
    {
        super(worldIn, x, y, z, fallingBlockState);

        failedBreakCheck = false;
        dontSetBlock = false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick()
    {
        final BlockState fallingBlockState = getBlockState();
        if (fallingBlockState.isAir())
        {
            remove();
        }
        else
        {
            Block block = fallingBlockState.getBlock();
            if (time++ == 0)
            {
                // First tick, replace the existing block
                BlockPos blockpos = blockPosition();
                if (level.getBlockState(blockpos).getBlock().isIn(block))
                {
                    level.removeBlock(blockpos, false);
                }
                else if (!level.isClientSide)
                {
                    remove();
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
                        remove();
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
                        else if (!level.isEmptyBlock(posAt.down()) && canFallThrough(level, posAt.down(), level.getBlockState(posAt.down())))
                        {
                            level.destroyBlock(posAt.down(), true);
                            failedBreakCheck = true;
                            return;
                        }
                    }

                    BlockState hitBlockState = level.getBlockState(posAt);
                    setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));

                    if (hitBlockState.getBlock() != Blocks.MOVING_PISTON)
                    {
                        remove();
                        if (!dontSetBlock)
                        {
                            if (hitBlockState.canBeReplaced(new DirectionalPlaceContext(this.level, posAt, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) && fallingBlockState.canBeReplacedByLeaves(this.level, posAt) && !FallingBlock.isFree(this.level.getBlockState(posAt.down())))
                            {
                                if (fallingBlockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(posAt).getType() == Fluids.WATER)
                                {
                                    ((FallingBlockEntityAccessor) this).accessor$setBlockState(fallingBlockState.with(BlockStateProperties.WATERLOGGED, Boolean.TRUE));
                                }

                                if (level.setBlockAndUpdate(posAt, fallingBlockState))
                                {
                                    if (block instanceof FallingBlock)
                                    {
                                        ((FallingBlock) block).onLand(this.level, posAt, fallingBlockState, hitBlockState, this);
                                    }

                                    if (TFCTags.Blocks.CAN_LANDSLIDE.contains(fallingBlockState.getBlock()))
                                    {
                                        level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(posAt));
                                    }

                                    // Sets the tile entity if it exists
                                    if (blockData != null && fallingBlockState.hasTileEntity())
                                    {
                                        TileEntity tileEntity = level.getTileEntity(posAt);
                                        if (tileEntity != null)
                                        {
                                            CompoundNBT tileEntityData = tileEntity.save(new CompoundNBT());
                                            for (String dataKey : tileEntityData.getAllKeys())
                                            {
                                                INBT dataElement = tileEntityData.get(dataKey);
                                                if (!"x".equals(dataKey) && !"y".equals(dataKey) && !"z".equals(dataKey) && dataElement != null)
                                                {
                                                    tileEntityData.put(dataKey, dataElement.copy());
                                                }
                                            }
                                            tileEntity.load(fallingBlockState, tileEntityData);
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