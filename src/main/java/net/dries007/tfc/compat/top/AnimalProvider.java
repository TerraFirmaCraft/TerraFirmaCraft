package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.common.IShearable;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarFormatted;
import net.dries007.tfc.util.climate.ClimateTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class AnimalProvider implements Function<ITheOneProbe, Void>, IProbeInfoEntityProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_animal_provider";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeHitEntityData)
    {
        if (entity instanceof IAnimalTFC)
        {
            IAnimalTFC animal = (IAnimalTFC) entity;
            boolean familiarized = animal.getFamiliarity() > 0.15f;
            if (animal.getAdultFamiliarityCap() > 0)
            {
                TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation(familiarized ? "waila.tfc.animal.familiarized" : "waila.tfc.animal.not_familiarized").getFormattedText());
            }
            switch (animal.getAge())
            {
                case CHILD:
                    String date = ICalendarFormatted.getTimeAndDate(
                        ICalendar.TICKS_IN_DAY * (animal.getBirthDay() + animal.getDaysToAdulthood())
                        , CalendarTFC.CALENDAR_TIME.getDaysInMonth());
                    TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation("waila.tfc.animal.childhood_end", date).getFormattedText());
                    break;
                case OLD:
                    TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation("waila.tfc.animal.old").getFormattedText());
                    break;
                case ADULT:
                    if (familiarized)
                    {
                        if (animal.isReadyToMate())
                        {
                            TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation("waila.tfc.animal.can_mate").getFormattedText());
                        }
                        if (animal.isFertilized())
                        {

                            NBTTagCompound nbt = entity.getEntityData();
                            long pregnancyDate = nbt.getLong("pregnant");
                            if (pregnancyDate > 0)
                            {
                                String enddate = ICalendarFormatted.getTimeAndDate(
                                    ICalendar.TICKS_IN_DAY * (pregnancyDate + 240), CalendarTFC.CALENDAR_TIME.getDaysInMonth());
                                TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation("waila.tfc.animal.pregnancy_end", enddate).getFormattedText());
                            }
                        }
                        if (animal.isReadyForAnimalProduct())
                        {
                            if (animal instanceof IShearable)
                            {
                                TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation("waila.tfc.animal.can_shear").getFormattedText());
                            }
                            else if (animal.getType() == IAnimalTFC.Type.OVIPAROUS)
                            {
                                TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation("waila.tfc.animal.has_eggs").getFormattedText());
                            }
                            else
                            {
                                TOPPlugin.outputHorizontalText(iProbeInfo,new TextComponentTranslation("waila.tfc.animal.has_milk").getFormattedText());
                            }
                        }
                    }
                    break;
            }
        }
    }


    @Nullable
    @Override
    public Void apply(ITheOneProbe iTheOneProbe)
    {
        iTheOneProbe.registerEntityProvider(this);
        return null;
    }
}
