/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import joptsimple.internal.Strings;
import mcjty.theoneprobe.api.*;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;
import scala.actors.threadpool.Arrays;

/**
 * Does the direct "translation" from IWailaBlock to The One Probe
 */
public class TOPBlockInterface implements IProbeInfoProvider, IBlockDisplayOverride
{
    protected final IWailaBlock internal;

    public TOPBlockInterface(IWailaBlock internal)
    {
        this.internal = internal;
    }

    @Override
    public String getID()
    {
        return "top.tfc." + internal.getClass().getName();
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData hitData)
    {
        boolean stop = true;
        BlockPos pos = hitData.getPos();
        TileEntity tileEntity = world.getTileEntity(pos);
        for(Class<?> bodyClass : internal.getBodyClassList())
        {
            if(bodyClass.isInstance(state.getBlock()) || bodyClass.isInstance(tileEntity))
            {
                stop = false;
                break;
            }
        }
        if(stop)
        {
            for (Class<?> tailClass : internal.getTailClassList())
            {
                if (tailClass.isInstance(state.getBlock()) || tailClass.isInstance(tileEntity))
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

        NBTTagCompound nbt = new NBTTagCompound();
        if(tileEntity != null)
        {
            nbt = tileEntity.writeToNBT(nbt);
        }

        List<String> bodyTooltip = new ArrayList<>();
        bodyTooltip = internal.getBodyTooltip(world, hitData.getPos(), bodyTooltip, nbt);
        for(String string : bodyTooltip)
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text(string);
        }

        List<String> tailTooltip = new ArrayList<>();
        tailTooltip = internal.getTailTooltip(world, hitData.getPos(), tailTooltip, nbt);
        for(String string : tailTooltip)
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text(string);
        }
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData hitData)
    {
        boolean stop = true;
        BlockPos pos = hitData.getPos();
        TileEntity tileEntity = world.getTileEntity(pos);
        for (Class<?> stackClass : internal.getStackClassList())
        {
            if (stackClass.isInstance(state.getBlock()) || stackClass.isInstance(tileEntity))
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

        NBTTagCompound nbt = new NBTTagCompound();
        if(tileEntity != null)
        {
            nbt = tileEntity.writeToNBT(nbt);
        }

        ItemStack stack = internal.getStack(world, pos, nbt);

        if(!stack.isEmpty())
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(stack)
                .vertical()
                .itemLabel(stack)
                .text(TextStyleClass.MODNAME + TerraFirmaCraft.MOD_NAME);
            return true;
        }
        return false;
    }

    public boolean overridesHeadInfo()
    {
        return !internal.getStackClassList().isEmpty();
    }
}
