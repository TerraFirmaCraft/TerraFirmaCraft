/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import java.util.EnumSet;
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
import net.dries007.tfc.common.blocks.rotation.HandWheelBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.SourceNode;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class HandWheelBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements RotatingBlockEntity
{
    public static final int MAX_ROTATION_TICKS = 80;
    public static final float SPEED = Mth.TWO_PI / MAX_ROTATION_TICKS;

    public static void serverTick(Level level, BlockPos pos, BlockState state, HandWheelBlockEntity wheel)
    {
        wheel.checkForLastTickSync();
        clientTick(level, pos, state, wheel);

        if (wheel.needsStateUpdate)
        {
            wheel.updateWheel();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, HandWheelBlockEntity wheel)
    {
        if (wheel.rotationTimer > 0)
        {
            wheel.rotationTimer--;
            wheel.node.rotation().tick();
            if (wheel.rotationTimer == 0)
            {
                wheel.node.rotation().reset();
            }
        }
    }

    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.hand_wheel");
    private static final int SLOT_WHEEL = 0;

    private final SourceNode node;

    private int rotationTimer = 0;
    private boolean needsStateUpdate = false;
    private boolean invalid;

    public HandWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);

        // Hand wheel only connects, and outputs, to a single direction.
        final Direction outputDirection = state.getValue(HandWheelBlock.FACING);

        this.invalid = false;
        this.node = new SourceNode(pos, EnumSet.of(outputDirection), outputDirection.getOpposite(), 0) {
            @Override
            public String toString()
            {
                return "HandWheel[pos=%s, direction=%s]".formatted(pos(), outputDirection);
            }
        };
    }

    public HandWheelBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.HAND_WHEEL.get(), pos, state);
    }

    public ItemStack viewStack()
    {
        return inventory.getStackInSlot(SLOT_WHEEL);
    }

    public void rotate()
    {
        assert level != null;

        if (rotationTimer == 0)
        {
            node.rotation().set(0, SPEED);
        }
        rotationTimer = MAX_ROTATION_TICKS;
        markForSync();
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsStateUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack.getItem(), TFCTags.Items.HAND_WHEEL);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        rotationTimer = nbt.getInt("rotationTimer");
        invalid = nbt.getBoolean("invalid");
        super.loadAdditional(nbt, provider);
        needsStateUpdate = true;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putInt("rotationTimer", rotationTimer);
        nbt.putBoolean("invalid", invalid);
        super.saveAdditional(nbt, provider);
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void markAsInvalidInNetwork()
    {
        invalid = true;
    }

    @Override
    public boolean isInvalidInNetwork()
    {
        return invalid;
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }

    private boolean hasWheel()
    {
        return !inventory.getStackInSlot(SLOT_WHEEL).isEmpty();
    }

    private void updateWheel()
    {
        assert level != null;

        final BlockState state = getBlockState();
        final BlockState newState = state.setValue(HandWheelBlock.HAS_WHEEL, hasWheel());

        level.setBlockAndUpdate(worldPosition, newState);
        needsStateUpdate = false;
    }
}
