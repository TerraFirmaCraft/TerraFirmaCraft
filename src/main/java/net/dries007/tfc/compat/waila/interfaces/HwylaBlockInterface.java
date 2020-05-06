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
        for (Class<?> headClass : internal.getHeadClassList())
        {
            registrar.registerHeadProvider(this, headClass);
        }
        for (Class<?> bodyClass : internal.getBodyClassList())
        {
            registrar.registerBodyProvider(this, bodyClass);
        }
        for (Class<?> tailClass : internal.getTailClassList())
        {
            registrar.registerTailProvider(this, tailClass);
        }
        for (Class<?> stackClass : internal.getStackClassList())
        {
            registrar.registerStackProvider(this, stackClass);
        }
        for (Class<?> nbtClass : internal.getNBTClassList())
        {
            registrar.registerNBTProvider(this, nbtClass);
        }
    }

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getStack(accessor.getWorld(), accessor.getPosition(), accessor.getNBTData());
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getHeadTooltip(accessor.getWorld(), accessor.getPosition(), accessor.getNBTData());
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getBodyTooltip(accessor.getWorld(), accessor.getPosition(), currentTooltip, accessor.getNBTData());
    }

    @Nonnull
    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getTailTooltip(accessor.getWorld(), accessor.getPosition(), currentTooltip, accessor.getNBTData());
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        return te.writeToNBT(tag);
    }
}