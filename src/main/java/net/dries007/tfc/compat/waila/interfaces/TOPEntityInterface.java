/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;

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
        boolean stop = true;
        if(entity == null)
        {
            return;
        }
        for(Class<?> bodyClass : internal.getBodyClassList())
        {
            if(bodyClass.isInstance(entity))
            {
                stop = false;
                break;
            }
        }
        if(stop)
        {
            for (Class<?> tailClass : internal.getTailClassList())
            {
                if (tailClass.isInstance(entity))
                {
                    stop = false;
                    break;
                }
            }
        }
        // Player isn't looking at one of the providers
        if(stop)
        {
            return;
        }
        NBTTagCompound nbt = entity.writeToNBT(new NBTTagCompound());

        List<String> bodyTooltip = new ArrayList<>();
        bodyTooltip = internal.getBodyTooltip(entity, bodyTooltip, nbt);
        for(String string : bodyTooltip)
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text(string);
        }

        List<String> tailTooltip = new ArrayList<>();
        tailTooltip = internal.getTailTooltip(entity, tailTooltip, nbt);
        for(String string : tailTooltip)
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text(string);
        }
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, Entity entity, IProbeHitEntityData hitData)
    {
        boolean stop = true;
        if(entity == null)
        {
            return false;
        }
        for(Class<?> bodyClass : internal.getBodyClassList())
        {
            if(bodyClass.isInstance(entity))
            {
                stop = false;
                break;
            }
        }
        // Player isn't looking at one of the providers
        if(stop)
        {
            return false;
        }
        boolean override = false;
        NBTTagCompound nbt = entity.writeToNBT(new NBTTagCompound());

        List<String> headTooltip = new ArrayList<>();
        headTooltip = internal.getHeadTooltip(entity, headTooltip, nbt);

        for(String string : headTooltip)
        {
            override = true;
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text(string);
        }
        return override;
    }

    public boolean overridesHeadInfo()
    {
        return !internal.getHeadClassList().isEmpty();
    }
}
