/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.util.calendar.CalendarTFC;

@ParametersAreNonnullByDefault
public class TETickCounter extends TEBase
{
    private long lastUpdateTick;

    public long getTicksSinceUpdate()
    {
        return CalendarTFC.PLAYER_TIME.getTicks() - lastUpdateTick;
    }

    public void resetCounter()
    {
        lastUpdateTick = CalendarTFC.PLAYER_TIME.getTicks();
        markForSync();
    }

    public void reduceCounter(long amount)
    {
        lastUpdateTick += amount;
        markForSync();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        lastUpdateTick = nbt.getLong("tick");
        super.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("tick", lastUpdateTick);
        return super.writeToNBT(nbt);
    }
}
