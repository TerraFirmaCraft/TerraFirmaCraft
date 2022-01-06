package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.crop.DoubleCropBlock;
import net.dries007.tfc.common.blocks.crop.ICropBlock;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public class CropBlockEntity extends TickCounterBlockEntity implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        crop.checkForCalendarUpdate();
    }

    public static void serverTickBottomPartOnly(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        if (state.getValue(DoubleCropBlock.PART) == DoubleCropBlock.Part.BOTTOM)
        {
            crop.checkForCalendarUpdate();
        }
    }

    private float growth;
    private float yield;
    private float expiry;

    public CropBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CROP.get(), pos, state);
    }

    public CropBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        final BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof ICropBlock crop)
        {
            crop.growthTick(level, worldPosition, state, this);
        }
    }

    public float getGrowth()
    {
        return growth;
    }

    public float getYield()
    {
        return yield;
    }

    public float getExpiry()
    {
        return expiry;
    }

    public void setGrowth(float growth)
    {
        this.growth = growth;
        markForSync();
    }

    public void setYield(float yield)
    {
        this.yield = yield;
        markForSync();
    }

    public void setExpiry(float expiry)
    {
        this.expiry = expiry;
        markForSync();
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        lastUpdateTick = tick;
        markForSync();
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        growth = nbt.getFloat("growth");
        yield = nbt.getFloat("yield");
        expiry = nbt.getFloat("expiry");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("growth", growth);
        nbt.putFloat("yield", yield);
        nbt.putFloat("expiry", expiry);
        super.saveAdditional(nbt);
    }
}
