/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import mcp.mobius.waila.api.*;

/**
 * Does the direct "translation" from IWailaBlock to Hwyla
 */
public class HwylaBlockInterface implements IWailaDataProvider, IWailaPlugin
{
    protected final IWailaBlock internal;

    public HwylaBlockInterface(IWailaBlock internal)
    {
        this.internal = internal;
    }


    @Override
    public void register(IWailaRegistrar registrar)
    {
        // Register providers accordingly to each implementation
        for (Class<?> clazz : internal.getLookupClass())
        {
            registrar.registerBodyProvider(this, clazz);
            if (TileEntity.class.isAssignableFrom(clazz))
            {
                // Register to update NBT data on all tile entities.
                registrar.registerNBTProvider(this, clazz);
            }
            if (internal.overrideTitle())
            {
                registrar.registerHeadProvider(this, clazz);
            }
            if (internal.overrideIcon())
            {
                registrar.registerStackProvider(this, clazz);
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getIcon(accessor.getWorld(), accessor.getPosition(), accessor.getNBTData());
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        String title = internal.getTitle(accessor.getWorld(), accessor.getPosition(), accessor.getNBTData());
        if (!title.isEmpty())
        {
            currentTooltip.clear();
            currentTooltip.add(TextFormatting.WHITE.toString() + title);
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        currentTooltip.addAll(internal.getTooltip(accessor.getWorld(), accessor.getPosition(), accessor.getNBTData()));
        return currentTooltip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        return te.writeToNBT(tag);
    }
}