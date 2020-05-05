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
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.objects.blocks.devices.BlockBlastFurnace;
import net.dries007.tfc.objects.te.TEBlastFurnace;

@WailaPlugin
public class BlastFurnaceProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockBlastFurnace && accessor.getTileEntity() instanceof TEBlastFurnace)
        {
            TEBlastFurnace blastFurnace = (TEBlastFurnace) accessor.getTileEntity();
            int chinmey = BlockBlastFurnace.getChimneyLevels(accessor.getWorld(), accessor.getPosition());
            if (chinmey > 0)
            {
                int maxItems = chinmey * 4;
                int oreStacks = blastFurnace.getOreStacks().size();
                int fuelStacks = blastFurnace.getFuelStacks().size();
                int temperature = blastFurnace.getField(TEBlastFurnace.FIELD_TEMPERATURE);
                String heatTooltip = Heat.getTooltip(temperature);
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.ores", oreStacks, maxItems).getFormattedText());
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.fuel", fuelStacks, maxItems).getFormattedText());
                if (heatTooltip != null)
                {
                    currentTooltip.add(heatTooltip);
                }
            }
            else
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.blast_furnace.not_formed").getFormattedText());
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
        registrar.registerBodyProvider(this, TEBlastFurnace.class);
        registrar.registerNBTProvider(this, TEBlastFurnace.class);
    }
}
