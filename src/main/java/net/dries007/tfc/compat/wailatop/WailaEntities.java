package net.dries007.tfc.compat.wailatop;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

import mcp.MethodsReturnNonnullByDefault;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalMammal;
import net.dries007.tfc.objects.entity.animal.EntityChickenTFC;
import net.dries007.tfc.objects.entity.animal.EntityCowTFC;
import net.dries007.tfc.objects.entity.animal.EntitySheepTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarFormatted;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WailaEntities implements IWailaEntityProvider
{
    public static void callbackRegister(IWailaRegistrar registrar)
    {
        WailaEntities dataProvider = new WailaEntities();

        registrar.registerBodyProvider(dataProvider, IAnimalTFC.class);

    }


    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {

        IAnimalTFC animal = (IAnimalTFC) entity;
        boolean familiarized = animal.getFamiliarity() > 0.15f;
        if (animal.getAdultFamiliarityCap() > 0)
        {
            currenttip.add(new TextComponentTranslation(familiarized ? "waila.tfc.familiarized" : "waila.tfc.notfamiliarized").getFormattedText());
        }
        switch (animal.getAge())
        {
            case CHILD:
                currenttip.add(new TextComponentTranslation("waila.tfc.childhoodend").getFormattedText()
                    + ": " + ICalendarFormatted.getTimeAndDate(
                    ICalendar.TICKS_IN_DAY * (animal.getBirthDay() + animal.getDaysToAdulthood())
                    , CalendarTFC.CALENDAR_TIME.getDaysInMonth()));
                break;
            case OLD:
                currenttip.add(new TextComponentTranslation("waila.tfc.old").getFormattedText());
                break;
            case ADULT:
                if (familiarized)
                {
                    if (animal.isReadyToMate())
                    {
                        currenttip.add(new TextComponentTranslation("waila.tfc.getbusy").getFormattedText());
                    }
                    if (animal.isFertilized())
                    {
                        NBTTagCompound nbt = accessor.getNBTData();
                        long pregnancyDate = nbt.getLong("pregnant");
                        if (pregnancyDate > 0)
                            currenttip.add(new TextComponentTranslation("waila.tfc.pregnancyend").getFormattedText()
                                + ": " + ICalendarFormatted.getTimeAndDate(
                                ICalendar.TICKS_IN_DAY * (pregnancyDate + 240), CalendarTFC.CALENDAR_TIME.getDaysInMonth()));
                    }
                    if (animal.isReadyForAnimalProduct())
                    {
                        if (animal.getGender() == IAnimalTFC.Gender.FEMALE && (animal instanceof EntityCowTFC || animal instanceof EntityChickenTFC))
                        {
                            currenttip.add(new TextComponentTranslation(
                                (animal instanceof EntityAnimalMammal) ? "waila.tfc.milked" : "waila.tfc.haseggs").getFormattedText());
                        }
                        if (animal instanceof EntitySheepTFC)
                        {
                            currenttip.add(new TextComponentTranslation("waila.tfc.canshear").getFormattedText());
                        }

                    }
                }
                break;
        }
        return currenttip;
    }


}
