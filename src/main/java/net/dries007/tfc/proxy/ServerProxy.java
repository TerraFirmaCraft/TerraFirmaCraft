/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.SERVER)
public class ServerProxy implements IProxy
{
    @Override
    @Nonnull
    public IThreadListener getThreadListener(MessageContext context)
    {
        if (context.side.isServer())
        {
            return context.getServerHandler().player.server;
        }
        else
        {
            throw new WrongSideException("Tried to get the IThreadListener from a client-side MessageContext on the dedicated server");
        }
    }

    @Override
    @Nullable
    public EntityPlayer getPlayer(MessageContext context)
    {
        if (context.side.isServer())
        {
            return context.getServerHandler().player;
        }
        else
        {
            throw new WrongSideException("Tried to get the player from a client-side MessageContext on the dedicated server");
        }
    }

    @Override
    @Nullable
    public World getWorld(MessageContext context)
    {
        if (context.side.isServer())
        {
            return context.getServerHandler().player.getServerWorld();
        }
        else
        {
            throw new WrongSideException("Tried to get the player from a client-side MessageContext on the dedicated server");
        }
    }
}
