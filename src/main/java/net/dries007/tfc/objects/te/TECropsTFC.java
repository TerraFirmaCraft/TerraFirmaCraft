/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.world.classic.CalendarTFC;

public class TECropsTFC extends TEBase
{
    private long timer;

    public TECropsTFC() { super (); }

    public long getHoursSincePlaced()
    {
        return (CalendarTFC.getTotalTime() - timer) / CalendarTFC.TICKS_IN_HOUR;
    }

    public void onPlaced()
    {
        timer = CalendarTFC.getTotalTime();
        this.markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        timer = nbt.getLong("timer");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("timer", timer);
        return super.writeToNBT(nbt);
    }
}
