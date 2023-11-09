/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.mechanical.HandWheelBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.RotationNetworkManager;
import net.dries007.tfc.util.rotation.SourceNode;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class HandWheelBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
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

    public HandWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);

        // Hand wheel only connects, and outputs, to a single direction.
        final Direction outputDirection = state.getValue(HandWheelBlock.FACING);
        final Rotation.Tickable rotation = Rotation.of(outputDirection.getOpposite(), 0);

        this.node = new SourceNode(pos, EnumSet.of(outputDirection), rotation) {
            @NotNull
            @Override
            public Rotation.Tickable rotation(Direction exitDirection)
            {
                assert exitDirection == outputDirection;
                return rotation;
            }

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

    @Override
    public void setRemoved()
    {
        assert level != null;
        super.setRemoved();
        RotationNetworkManager.remove(level, node);
    }

    @Override
    public void onChunkUnloaded()
    {
        assert level != null;
        super.onChunkUnloaded();
        RotationNetworkManager.remove(level, node);
    }

    @Override
    public void onLoad()
    {
        assert level != null;
        super.onLoad();
        RotationNetworkManager.addSource(level, node);
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

    public void updateWheel()
    {
        assert level != null;

        final BlockState state = getBlockState();
        final BlockState newState = state.setValue(HandWheelBlock.HAS_WHEEL, hasWheel());

        level.setBlockAndUpdate(worldPosition, newState);
        needsStateUpdate = false;
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
    public void loadAdditional(CompoundTag nbt)
    {
        rotationTimer = nbt.getInt("rotationTimer");
        super.loadAdditional(nbt);
        needsStateUpdate = true;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("rotationTimer", rotationTimer);
        super.saveAdditional(nbt);
    }

    public ItemStack viewStack()
    {
        return inventory.getStackInSlot(SLOT_WHEEL);
    }

    public float getRotationAngle(float partialTick)
    {
        return Rotation.angle(node.rotation(), partialTick);
    }

    public boolean hasWheel()
    {
        return !inventory.getStackInSlot(SLOT_WHEEL).isEmpty();
    }
}
