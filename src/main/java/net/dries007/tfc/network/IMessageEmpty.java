/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import io.netty.buffer.ByteBuf;

/**
 * Packets that don't need to send any data and just exist as signaling points
 */
public interface IMessageEmpty extends IMessage
{
    @Override
    default void fromBytes(ByteBuf buf) {}

    @Override
    default void toBytes(ByteBuf buf) {}
}
