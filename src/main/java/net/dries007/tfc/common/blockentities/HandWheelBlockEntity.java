package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.mechanical.HandWheelBlock;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.power.IRotationProvider;
import net.dries007.tfc.common.capabilities.power.RotationCapability;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class HandWheelBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, HandWheelBlockEntity wheel)
    {
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
            wheel.powered = false;
        }
        provideRotation(level, pos, state.getValue(HandWheelBlock.FACING), wheel.powered);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, HandWheelBlockEntity wheel)
    {
        if (wheel.rotationTimer > 0)
        {
            wheel.rotationTimer--;
        }
        if (wheel.rotationTimer == 0)
        {
            wheel.powered = false;
        }
    }

    public static void provideRotation(Level level, BlockPos pos, Direction direction, boolean powered)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(pos);
        for (int i = 1; i <= MAX_DRIVING_RANGE; i++)
        {
            cursor.move(direction);
            final BlockEntity blockEntity = level.getBlockEntity(cursor);
            if (blockEntity != null)
            {
                final boolean found = blockEntity.getCapability(RotationCapability.ROTATION, direction).map(cap -> {
                    if (cap.canBeDriven())
                    {
                        final boolean didPower = cap.setPowered(powered);
                        // avoid powering thru a gearbox into something else
                        if (cap.terminatesPowerTrain() && didPower)
                        {
                            return false;
                        }
                        return didPower;
                    }
                    return false;
                }).orElse(false);
                if (!found)
                {
                    return;
                }
            }
            else
            {
                return;
            }
        }
    }

    public static final int MAX_DRIVING_RANGE = 4;

    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.hand_wheel");
    private static final int SLOT_WHEEL = 0;

    private final SidedHandler.Builder<IRotationProvider> handler;

    private int rotationTimer = 0;
    private boolean powered;
    private boolean needsStateUpdate = false;

    public HandWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);
        handler = new SidedHandler.Builder<IRotationProvider>()
            .on(new HandWheelRotationHandler(this, Direction.NORTH), Direction.NORTH)
            .on(new HandWheelRotationHandler(this, Direction.SOUTH), Direction.SOUTH)
            .on(new HandWheelRotationHandler(this, Direction.WEST), Direction.WEST)
            .on(new HandWheelRotationHandler(this, Direction.EAST), Direction.EAST);
    }

    public HandWheelBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.HAND_WHEEL.get(), pos, state);
    }

    public void addRotation(int ticks)
    {
        rotationTimer += ticks;
        powered = true;
    }

    public boolean isPowered()
    {
        return powered;
    }

    public void setPowered(boolean powered)
    {
        this.powered = powered;
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
        if (cap == RotationCapability.ROTATION)
        {
            return handler.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    public static class HandWheelRotationHandler implements IRotationProvider
    {
        private final HandWheelBlockEntity wheel;
        private final Direction side;

        public HandWheelRotationHandler(HandWheelBlockEntity wheel, Direction side)
        {
            this.wheel = wheel;
            this.side = side;
        }

        public boolean isCorrectSide()
        {
            assert wheel.level != null;
            return side == wheel.level.getBlockState(wheel.getBlockPos()).getValue(HandWheelBlock.FACING);
        }

        @Override
        public boolean isPowered()
        {
            return isCorrectSide() && wheel.isPowered();
        }

        @Override
        public boolean setPowered(boolean powered)
        {
            if (isCorrectSide())
            {
                wheel.setPowered(powered);
                return true;
            }
            return false;
        }

        @Override
        public boolean canBeDriven()
        {
            return false;
        }
    }
}
