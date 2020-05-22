/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.dries007.tfc.objects.TFCTags;
import net.dries007.tfc.world.tracker.WorldTrackerCapability;

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
        return !state.isSolidSide(world, pos, Direction.UP);
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
        if (getBlockState().isAir())
        {
            remove();
        }
        else
        {
            Block block = getBlockState().getBlock();
            if (fallTime++ == 0)
            {
                // First tick, replace the existing block
                BlockPos blockpos = new BlockPos(this);
                if (world.getBlockState(blockpos).getBlock() == block)
                {
                    world.removeBlock(blockpos, false);
                }
                else if (!world.isRemote)
                {
                    remove();
                    return;
                }
            }

            if (!hasNoGravity())
            {
                setMotion(getMotion().add(0.0D, -0.04D, 0.0D));
            }

            move(MoverType.SELF, getMotion());

            if (!world.isRemote)
            {
                BlockPos posAt = new BlockPos(this);
                if (!onGround)
                {
                    failedBreakCheck = false;
                    if (fallTime > 100 && (posAt.getY() < 1 || posAt.getY() > 256) || fallTime > 600)
                    {
                        if (shouldDropItem && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                        {
                            entityDropItem(block);
                        }
                        remove();
                    }
                }
                else
                {
                    // On ground
                    if (!failedBreakCheck)
                    {
                        if (!world.isAirBlock(posAt) && canFallThrough(world, posAt, world.getBlockState(posAt)))
                        {
                            world.destroyBlock(posAt, true);
                            failedBreakCheck = true;
                            return;
                        }
                        else if (!world.isAirBlock(posAt.down()) && canFallThrough(world, posAt.down(), world.getBlockState(posAt.down())))
                        {
                            world.destroyBlock(posAt.down(), true);
                            failedBreakCheck = true;
                            return;
                        }
                    }

                    BlockState blockstate = world.getBlockState(posAt);
                    setMotion(getMotion().mul(0.7D, -0.5D, 0.7D));

                    if (blockstate.getBlock() != net.minecraft.block.Blocks.MOVING_PISTON)
                    {
                        remove();
                        if (!dontSetBlock)
                        {
                            if (blockstate.isReplaceable(new DirectionalPlaceContext(this.world, posAt, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) && getBlockState().isValidPosition(this.world, posAt) && !FallingBlock.canFallThrough(this.world.getBlockState(posAt.down())))
                            {
                                // todo: if we want this to work, we need an AT on fallTile
                                //if (getBlockState().has(BlockStateProperties.WATERLOGGED) && this.world.getFluidState(posAt).getFluid() == Fluids.WATER)
                                //{
                                //    this.fallTile = this.fallTile.with(BlockStateProperties.WATERLOGGED, Boolean.TRUE);
                                //}

                                if (world.setBlockState(posAt, getBlockState()))
                                {
                                    if (block instanceof FallingBlock)
                                    {
                                        ((FallingBlock) block).onEndFalling(this.world, posAt, getBlockState(), blockstate);
                                    }

                                    if (TFCTags.Blocks.CAN_LANDSLIDE.contains(getBlockState().getBlock()))
                                    {
                                        world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(posAt));
                                    }

                                    // Sets the tile entity if it exists
                                    if (tileEntityData != null && getBlockState().hasTileEntity())
                                    {
                                        TileEntity tileEntity = world.getTileEntity(posAt);
                                        if (tileEntity != null)
                                        {
                                            CompoundNBT tileEntityData = tileEntity.write(new CompoundNBT());
                                            for (String dataKey : tileEntityData.keySet())
                                            {
                                                INBT dataElement = tileEntityData.get(dataKey);
                                                if (!"x".equals(dataKey) && !"y".equals(dataKey) && !"z".equals(dataKey) && dataElement != null)
                                                {
                                                    tileEntityData.put(dataKey, dataElement.copy());
                                                }
                                            }
                                            tileEntity.read(tileEntityData);
                                            tileEntity.markDirty();
                                        }
                                    }
                                }
                                else if (shouldDropItem && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                                {
                                    entityDropItem(block);
                                }
                            }
                            else if (shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                            {
                                entityDropItem(block);
                            }
                        }
                        else if (block instanceof FallingBlock)
                        {
                            ((FallingBlock) block).onBroken(this.world, posAt);
                        }
                    }
                }
            }

            setMotion(getMotion().scale(0.98D));
        }
    }
}
