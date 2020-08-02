/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public class CalendarWorldData extends WorldSavedData
{
    private static final String NAME = MOD_ID + "_calendar";
    private static final String NAME_PRE_V1_5_0_X = MOD_ID + ":calendar";

    private static final Logger LOGGER = LogManager.getLogger();

    @Nonnull
    public static CalendarWorldData get(@Nonnull World world)
    {
        MapStorage mapStorage = world.getMapStorage();
        if (mapStorage != null)
        {
            CalendarWorldData data = (CalendarWorldData) mapStorage.getOrLoadData(CalendarWorldData.class, NAME);
            if (data == null)
            {
                // Unable to load data, so assign default values
                LOGGER.info("Creating default calendar world data.");
                data = new CalendarWorldData(NAME);
                data.markDirty();
                mapStorage.setData(NAME, data);

                // If present, try and load old data
                CalendarWorldData oldData = (CalendarWorldData) mapStorage.getOrLoadData(CalendarWorldData.class, NAME_PRE_V1_5_0_X);
                if (oldData != null)
                {
                    // Old data found. Copy it to the new location.
                    // We don't use WorldSavedData write / read methods as it will just write the current state of the calendar (which is not yet loaded)
                    LOGGER.info("Found v1.5.x save data, migrating it to new location.");
                    data.calendar.deserializeNBT(oldData.calendar.serializeNBT());
                }
            }
            return data;
        }
        throw new IllegalStateException("Unable to access calendar data - everything is wrong now");
    }

    private final CalendarTFC calendar;

    @SuppressWarnings("unused")
    public CalendarWorldData(String name)
    {
        super(name);
        this.calendar = new CalendarTFC();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        calendar.deserializeNBT(nbt.getCompoundTag("calendar"));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("calendar", CalendarTFC.INSTANCE.serializeNBT());
        return nbt;
    }

    /**
     * Since this updates every tick, and doesn't store a local copy always assume it needs saving to disk
     */
    @Override
    public boolean isDirty()
    {
        return true;
    }

    @Nonnull
    public CalendarTFC getCalendar()
    {
        return calendar;
    }
}
