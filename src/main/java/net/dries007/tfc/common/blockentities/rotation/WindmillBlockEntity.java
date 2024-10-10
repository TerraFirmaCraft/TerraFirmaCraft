/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.network.Action;
import net.dries007.tfc.util.network.RotationNetworkManager;
import net.dries007.tfc.util.network.RotationNode;
import net.dries007.tfc.util.network.RotationOwner;


public class WindmillBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements RotationOwner
{
    public static final int SLOTS = 5;
    public static final float MIN_SPEED = Mth.TWO_PI / (20 * 20);
    public static final float MAX_SPEED = Mth.TWO_PI / (8 * 20);

    public static void serverTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill)
    {
        windmill.checkForLastTickSync();
        if (windmill.needsStateUpdate)
        {
            windmill.updateState();
        }

        if (level.getGameTime() % 40 == 0 && isObstructedBySolidBlocks(level, pos, state.getValue(WindmillBlock.AXIS)))
        {
            // Check every two seconds if the windmill is obstructed, and if so, break
            level.destroyBlock(pos, true);
        }
    }

    public static boolean isObstructedBySolidBlocks(Level level, BlockPos pos, Direction.Axis axis)
    {
        // Check every two seconds if the windmill is obstructed, and if so, break
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dH = -6; dH <= 6; dH++)
        {
            for (int dy = -6; dy <= 6; dy++)
            {
                if (dH * dH + dy * dy < 7 * 7 && (dH != 0 || dy != 0))
                {
                    cursor.setWithOffset(pos, axis == Direction.Axis.X ? 0 : dH, dy, axis == Direction.Axis.Z ? 0 : dH);

                    final BlockState state = level.getBlockState(cursor);
                    if (!state.isAir() && !state.getCollisionShape(level, cursor).isEmpty())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final RotationNode node;
    private boolean needsStateUpdate = true;

    public WindmillBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.WINDMILL.get(), pos, state, defaultInventory(SLOTS));
    }

    public WindmillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<ItemStackHandler> inventory)
    {
        super(type, pos, state, inventory);

        // Windmills can have up to five blades added, which increase their maximum speed.
        // - Rotation speed interpolates as not to have a sharp jump between levels.
        // - Connections are static and only in the horizontal directions specified by the axis
        // - Rotation is always in the 'forward' direction (so windmills look somewhat consistent).
        final Direction.Axis axis = state.getValue(WindmillBlock.AXIS);

        this.node = new RotationNode.Axle(this, axis, RotationNetworkManager.WINDMILL_TORQUE)
        {
            @Override
            protected float providedSpeed()
            {
                return Mth.map(getBlockState().getValue(WindmillBlock.COUNT), 1, SLOTS, MIN_SPEED, MAX_SPEED);
            }

            @Override
            protected float providedTorque()
            {
                return RotationNetworkManager.WINDMILL_PROVIDED_TORQUE;
            }
        };
    }

    public int updateState()
    {
        assert level != null;
        needsStateUpdate = false;
        int count = 0;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                count++;
            }
        }
        if (count == 0)
        {
            BlockState axleState = ((WindmillBlock) getBlockState().getBlock()).getAxle().defaultBlockState();
            axleState = Helpers.copyProperties(axleState, getBlockState());
            level.setBlockAndUpdate(worldPosition, axleState);
        }
        else
        {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(WindmillBlock.COUNT, count));
        }
        return count;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsStateUpdate = true;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.WINDMILL_BLADES);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        node.saveAdditional(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        node.loadAdditional(tag);
    }

    @Override
    protected void loadAdditionalOnClient(CompoundTag tag, HolderLookup.Provider provider)
    {
        node.loadAdditionalOnClient(tag);
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(Action.ADD);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(Action.REMOVE);
    }

    @Override
    public RotationNode getRotationNode()
    {
        return node;
    }
}
