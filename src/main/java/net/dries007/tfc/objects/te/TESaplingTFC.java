/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.world.classic.CalendarTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TESaplingTFC extends TEBase
{
    private long timer;

    public TESaplingTFC()
    {
        super();
    }

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
    public void readFromNBT(NBTTagCompound tag)
    {
        timer = tag.getLong("timer");
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setLong("timer", timer);
        return super.writeToNBT(tag);
    }
}
