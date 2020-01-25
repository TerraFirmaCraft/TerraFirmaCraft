package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendarTickable;

@ParametersAreNonnullByDefault
public class TECropBase extends TETickCounter implements ICalendarTickable
{
    // static constants

    private static final String NBT_LAST_TICK_CAL_CHECKED = "lastTickCalChecked";

    // fields

    protected long _lastTickCalChecked;

    // constructor

    public TECropBase()
    {
        _lastTickCalChecked = CalendarTFC.PLAYER_TIME.getTicks();
    }

    // ICalendarUpdate methods

    @Override
    public void onCalendarUpdate(long playerTickDelta)
    {
        BlockCropTFC block = (BlockCropTFC) getBlockType();
        block.checkGrowth(world, pos, world.getBlockState(pos), world.rand);
    }

    @Override
    public long getLastUpdateTick()
    {
        return _lastTickCalChecked;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        _lastTickCalChecked = tick;
        markDirty();
    }

    // NBT TE method overrides

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey(NBT_LAST_TICK_CAL_CHECKED))
            _lastTickCalChecked = nbt.getLong(NBT_LAST_TICK_CAL_CHECKED);
        super.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong(NBT_LAST_TICK_CAL_CHECKED, _lastTickCalChecked);
        return super.writeToNBT(nbt);
    }
}
