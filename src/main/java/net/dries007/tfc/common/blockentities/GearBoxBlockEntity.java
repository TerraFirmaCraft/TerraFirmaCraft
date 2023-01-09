package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.power.IRotationProvider;
import net.dries007.tfc.common.capabilities.power.RotationCapability;
import net.dries007.tfc.util.Helpers;

// todo: this doesn't work properly. a gear box needs to have memory of what is drawing and what is expending power. axles have no such memory as their rotation is not axially aware.
public class GearBoxBlockEntity extends TFCBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, GearBoxBlockEntity box)
    {
        if (level.getGameTime() % 20 == 0)
        {
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (state.getValue(DirectionPropertyBlock.PROPERTY_BY_DIRECTION.get(direction)))
                {
                    cursor.setWithOffset(pos, direction);
                    final BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity != null)
                    {
                        blockEntity.getCapability(RotationCapability.ROTATION).ifPresent(cap -> {
                            if (cap.canBeDriven())
                            {
                                HandWheelBlockEntity.provideRotation(level, pos, direction, box.powered);
                            }
                        });
                    }
                }
            }
        }

    }

    private final SidedHandler.Builder<IRotationProvider> handler;

    public GearBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        handler = new SidedHandler.Builder<>();
        Arrays.stream(Helpers.DIRECTIONS).forEach(dir -> handler.on(new GearBoxHandler(this, dir), dir));
    }

    private boolean powered = false;

    public GearBoxBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.GEAR_BOX.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        powered = tag.getBoolean("powered");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putBoolean("powered", powered);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == RotationCapability.ROTATION)
        {
            return handler.getSidedHandler(side).cast();
        }
        return super.getCapability(cap);
    }

    public static class GearBoxHandler implements IRotationProvider
    {
        private final GearBoxBlockEntity box;
        private final BooleanProperty property;

        public GearBoxHandler(GearBoxBlockEntity box, Direction direction)
        {
            this.box = box;
            this.property = DirectionPropertyBlock.PROPERTY_BY_DIRECTION.get(direction);
        }

        public boolean isCorrectDirection()
        {
            assert box.level != null;
            final BlockState state = box.level.getBlockState(box.getBlockPos());
            return state.getValue(property);
        }

        @Override
        public boolean isPowered()
        {
            return isCorrectDirection() && box.powered;
        }

        @Override
        public boolean setPowered(boolean powered)
        {
            if (isCorrectDirection())
            {
                box.powered = powered;
                return true;
            }
            return false;
        }

        @Override
        public boolean terminatesPowerTrain()
        {
            return true;
        }
    }
}
