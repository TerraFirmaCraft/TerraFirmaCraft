package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PitKilnProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_pitkiln_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        if (world.getTileEntity(iProbeHitData.getPos()) instanceof TEPitKiln)
        {
            TEPitKiln te = Helpers.getTE(world, iProbeHitData.getPos(), TEPitKiln.class);
            boolean isLit = te.isLit();

            if (isLit)
            {
                long remainingTicks = ConfigTFC.GENERAL.pitKilnTime - (CalendarTFC.PLAYER_TIME.getTicks() - te.getLitTick());
                long remainingMinutes = Math.round(remainingTicks / 1200.0f);
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.devices.remaining", remainingMinutes).getFormattedText());
            }
            else
            {
                int straw = te.getStrawCount();
                int logs = te.getLogCount();
                if (straw == 8 && logs == 8)
                {
                    TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.pitkiln.unlit").getFormattedText());
                }
                else
                {
                    if (straw < 8)
                    {
                        TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.pitkiln.straw", 8 - straw).getFormattedText());
                    }
                    if (logs < 8)
                    {
                        TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.pitkiln.logs", 8 - logs).getFormattedText());
                    }
                }
            }
        }
    }


    @Nullable
    @Override
    public Void apply(ITheOneProbe iTheOneProbe)
    {
        iTheOneProbe.registerProvider(this);
        return null;
    }
}
