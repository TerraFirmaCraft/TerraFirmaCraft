/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.mechanical.HandWheelBlock;
import net.dries007.tfc.common.capabilities.power.RotationCapability;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.mechanical.MechanicalUniverse;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class HandWheelBlockEntity extends RotatingInventoryBlockEntity<ItemStackHandler>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, HandWheelBlockEntity wheel)
    {
        wheel.checkForLastTickSync();
        if (wheel.needsStateUpdate)
        {
            wheel.updateWheel();
        }
        if (wheel.rotationTimer > 0)
        {
            wheel.rotationTimer--;
        }
        if (wheel.rotationTimer == 0)
        {
            wheel.setPowered(false);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, HandWheelBlockEntity wheel)
    {
        if (wheel.rotationTimer > 0)
        {
            wheel.rotationTimer--;
        }
        if (wheel.rotationTimer == 0 && wheel.isPowered())
        {
            wheel.setPowered(false);
        }
    }

    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.hand_wheel");
    private static final int SLOT_WHEEL = 0;

    private int rotationTimer = 0;
    private boolean powered;
    private boolean needsStateUpdate = false;

    public HandWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);
    }

    public HandWheelBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.HAND_WHEEL.get(), pos, state);
    }

    public void addRotation(int ticks)
    {
        assert level != null;
        rotationTimer += ticks;
        powered = true;
        if (!level.isClientSide)
        {
            MechanicalUniverse.getOrCreate(this);
        }
    }

    public boolean isPowered()
    {
        return powered;
    }

    public void setPowered(boolean powered)
    {
        assert level != null;
        this.powered = powered;
        if (!powered && level.isClientSide)
        {
            MechanicalUniverse.delete(this);
        }
        markForSync();
    }

    public void updateWheel()
    {
        assert level != null;
        final BlockState state = level.getBlockState(worldPosition);
        final BlockState newState = Helpers.setProperty(state, HandWheelBlock.HAS_WHEEL, hasWheel());
        if (hasWheel() != state.getValue(HandWheelBlock.HAS_WHEEL))
        {
            level.setBlockAndUpdate(worldPosition, newState);
        }
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
        powered = nbt.getBoolean("powered");
        super.loadAdditional(nbt);
        needsStateUpdate = true;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("rotationTimer", rotationTimer);
        nbt.putBoolean("powered", powered);
        super.saveAdditional(nbt);
    }

    public ItemStack viewStack()
    {
        return inventory.getStackInSlot(SLOT_WHEEL);
    }

    public int getRotationTimer()
    {
        return rotationTimer;
    }

    public boolean hasWheel()
    {
        return !inventory.getStackInSlot(SLOT_WHEEL).isEmpty();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == RotationCapability.ROTATION && (side == null || isCorrectDirection(side)))
        {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public boolean isCorrectDirection(Direction side)
    {
        return getBlockState().getValue(HandWheelBlock.FACING) == side;
    }

    @Override
    public boolean hasShaft(LevelAccessor level, BlockPos pos, Direction facing)
    {
        return isCorrectDirection(facing);
    }

    @Override
    public boolean isSource()
    {
        return true;
    }

    @Override
    public int getSignal()
    {
        return isPowered() ? 4 : 0;
    }

    @Override
    public void setSignal(int signal) { }

}