/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IProxy
{
    IThreadListener getThreadListener(MessageContext context);

    EntityPlayer getPlayer(MessageContext context);

    class WrongSideException extends RuntimeException
    {
        WrongSideException(String message)
        {
            super(message);
        }
    }
}
