/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.calendar.CalendarTFC;

@WailaPlugin
public class PitKilnProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TEPitKiln)
        {
            TEPitKiln te = (TEPitKiln) accessor.getTileEntity();
            boolean isLit = te.isLit();

            if (isLit)
            {
                long remainingTicks = ConfigTFC.GENERAL.pitKilnTime - (CalendarTFC.PLAYER_TIME.getTicks() - te.getLitTick());
                long remainingMinutes = Math.round(remainingTicks / 1200.0f);
                currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.remaining", remainingMinutes).getFormattedText());
            }
            else
            {
                int straw = te.getStrawCount();
                int logs = te.getLogCount();
                if (straw == 8 && logs == 8)
                {
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.pitkiln.unlit").getFormattedText());
                }
                else
                {
                    if (straw < 8)
                    {
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.pitkiln.straw", 8 - straw).getFormattedText());
                    }
                    if (logs < 8)
                    {
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.pitkiln.logs", 8 - logs).getFormattedText());
                    }
                }
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        return te.writeToNBT(tag);
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(this, TEPitKiln.class);
        registrar.registerNBTProvider(this, TEPitKiln.class);
    }
}
