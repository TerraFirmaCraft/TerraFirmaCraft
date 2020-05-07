/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.TerraFirmaCraft;

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
        BlockPos pos = hitData.getPos();
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!isLookingAtProvider(state.getBlock(), tileEntity))
        {
            return;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        if (tileEntity != null)
        {
            nbt = tileEntity.writeToNBT(nbt);
        }

        List<String> tooltip = internal.getTooltip(world, hitData.getPos(), nbt);
        for (String string : tooltip)
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text(string);
        }
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData hitData)
    {
        BlockPos pos = hitData.getPos();
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!isLookingAtProvider(state.getBlock(), tileEntity))
        {
            return false;
        }

        NBTTagCompound nbt = new NBTTagCompound();
        if (tileEntity != null)
        {
            nbt = tileEntity.writeToNBT(nbt);
        }

        ItemStack stack = internal.overrideIcon() ? internal.getIcon(world, pos, nbt) : ItemStack.EMPTY;
        if (stack.isEmpty())
        {
            stack = hitData.getPickBlock();
        }
        String title = internal.overrideTitle() ? internal.getTitle(world, pos, nbt) : "";
        if (title.isEmpty())
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(stack)
                .vertical()
                .itemLabel(stack)
                .text(TextStyleClass.MODNAME + TerraFirmaCraft.MOD_NAME);
        }
        else
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(stack)
                .vertical()
                .text(TextStyleClass.NAME + title)
                .text(TextStyleClass.MODNAME + TerraFirmaCraft.MOD_NAME);
        }
        return true;
    }

    public boolean overridesHeadInfo()
    {
        return internal.overrideIcon() || internal.overrideTitle();
    }

    protected boolean isLookingAtProvider(Block block, TileEntity tileEntity)
    {
        for (Class<?> clazz : internal.getLookupClass())
        {
            if (clazz.isInstance(block) || clazz.isInstance(tileEntity))
            {
                return true;
            }
        }
        return false;
    }
}
