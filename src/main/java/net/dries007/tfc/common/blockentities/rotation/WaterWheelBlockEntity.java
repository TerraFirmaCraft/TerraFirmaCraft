/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WaterWheelBlock;
import net.dries007.tfc.util.Helpers;

public class WaterWheelBlockEntity extends TFCBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterWheelBlockEntity wheel)
    {
        if (level.getGameTime() % 20 == 0)
        {
            if (!WaterWheelBlock.waterWheelValid(level, pos, state))
            {
                level.destroyBlock(pos, true);
            }
            wheel.checkPowered();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WaterWheelBlockEntity wheel)
    {
        if (wheel.powered)
        {
            wheel.ticks++;
        }
    }

    private int ticks = 0;
    private boolean powered = false;
    private boolean inverted = false;

    public WaterWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public WaterWheelBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.WATER_WHEEL.get(), pos, state);
    }

    public boolean isPowered()
    {
        return powered;
    }

    public void checkPowered()
    {
        assert level != null;
        final boolean wasPower = powered;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Direction dir = getBlockState().getValue(WaterWheelBlock.AXIS) == Direction.Axis.X ? Direction.NORTH : Direction.WEST;

        for (int i = -1; i <= 1; i++)
        {
            cursor.set(worldPosition).move(dir, i).move(0, -2, 0);
            final BlockState state = level.getBlockState(cursor);
            final FluidState fluid = state.getFluidState();
            if (!Helpers.isFluid(fluid, FluidTags.WATER) || fluid.getFlow(level, cursor).lengthSqr() == 0 || !fluid.isSource())
            {
                powered = false;
                if (wasPower)
                {
                    markForSync();
                }
                return;
            }
        }
        powered = true;
        if (!wasPower)
        {
            markForSync();
        }
    }

    public int getTicks()
    {
        return ticks;
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        powered = tag.getBoolean("powered");
        inverted = tag.getBoolean("inverted");
        super.loadAdditional(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        tag.putBoolean("powered", powered);
        tag.putBoolean("inverted", inverted);
        super.saveAdditional(tag);
    }

    public boolean isCorrectDirection(Direction side)
    {
        return side.getAxis() == getBlockState().getValue(WaterWheelBlock.AXIS);
    }
}
