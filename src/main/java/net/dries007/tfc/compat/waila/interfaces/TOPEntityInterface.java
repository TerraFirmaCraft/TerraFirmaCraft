/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.TerraFirmaCraft;

/**
 * Does the direct "translation" from IWailaEntity to The One Probe
 */
public class TOPEntityInterface implements IProbeInfoEntityProvider, IEntityDisplayOverride
{
    protected final IWailaEntity internal;

    public TOPEntityInterface(IWailaEntity internal)
    {
        this.internal = internal;
    }

    @Override
    public String getID()
    {
        return "top.tfc." + internal.getClass().getName();
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, Entity entity, IProbeHitEntityData hitData)
    {
        if (entity == null)
        {
            return;
        }
        if (!isLookingAtProvider(entity))
        {
            return;
        }

        NBTTagCompound nbt = entity.writeToNBT(new NBTTagCompound());

        List<String> tooltip = internal.getTooltip(entity, nbt);
        for (String string : tooltip)
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text(string);
        }
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, Entity entity, IProbeHitEntityData hitData)
    {
        if (entity == null)
        {
            return false;
        }
        if (!isLookingAtProvider(entity))
        {
            return false;
        }
        NBTTagCompound nbt = entity.writeToNBT(new NBTTagCompound());

        String title = internal.getTitle(entity, nbt);

        if (title.isEmpty())
        {
            return false;
        }
        else
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .vertical()
                .text(TextStyleClass.NAME + title)
                .text(TextStyleClass.MODNAME + TerraFirmaCraft.MOD_NAME);
            return true;
        }
    }

    public boolean overridesHeadInfo()
    {
        return internal.overrideTitle();
    }

    protected boolean isLookingAtProvider(Entity entity)
    {
        for (Class<?> clazz : internal.getLookupClass())
        {
            if (clazz.isInstance(entity))
            {
                return true;
            }
        }
        return false;
    }
}
