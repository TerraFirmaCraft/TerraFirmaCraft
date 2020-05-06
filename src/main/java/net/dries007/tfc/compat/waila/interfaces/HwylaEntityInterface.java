/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import mcp.mobius.waila.api.*;

/**
 * Does the direct "translation" from IWailaEntity to Hwyla
 */
public class HwylaEntityInterface implements IWailaEntityProvider, IWailaPlugin
{
    protected final IWailaEntity internal;

    public HwylaEntityInterface(IWailaEntity internal)
    {
        this.internal = internal;
    }


    @Override
    public void register(IWailaRegistrar registrar)
    {
        // Register providers accordingly to each implementation
        for(Class<?> headClass : internal.getHeadClassList())
        {
            registrar.registerHeadProvider(this, headClass);
        }
        for(Class<?> bodyClass : internal.getBodyClassList())
        {
            registrar.registerBodyProvider(this, bodyClass);
        }
        for(Class<?> tailClass : internal.getTailClassList())
        {
            registrar.registerTailProvider(this, tailClass);
        }
        for(Class<?> nbtClass : internal.getNBTClassList())
        {
            registrar.registerTailProvider(this, nbtClass);
        }
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(Entity entity, List<String> currentTooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getHeadTooltip(entity, currentTooltip, accessor.getNBTData());
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(Entity entity, List<String> currentTooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getBodyTooltip(entity, currentTooltip, accessor.getNBTData());
    }

    @Nonnull
    @Override
    public List<String> getWailaTail(Entity entity, List<String> currentTooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {
        return internal.getTailTooltip(entity, currentTooltip, accessor.getNBTData());
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world)
    {
        return ent.writeToNBT(tag);
    }
}
