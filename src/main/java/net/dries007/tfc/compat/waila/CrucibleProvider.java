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
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.te.TECrucible;

@WailaPlugin
public class CrucibleProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TECrucible)
        {
            TECrucible crucible = (TECrucible) accessor.getTileEntity();
            if(crucible.getAlloy().getAmount() > 0)
            {
                Metal metal = crucible.getAlloyResult();
                currentTooltip.add(new TextComponentTranslation("waila.tfc.metal.output", crucible.getAlloy().getAmount(), new TextComponentTranslation(metal.getTranslationKey()).getFormattedText()).getFormattedText());
            }
            float temperature = accessor.getNBTData().getFloat("temp");
            String heatTooltip = Heat.getTooltip(temperature);
            if (heatTooltip != null)
            {
                currentTooltip.add(heatTooltip);
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
        registrar.registerBodyProvider(this, TECrucible.class);
        registrar.registerNBTProvider(this, TECrucible.class);
    }
}
