/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import net.dries007.tfc.common.container.ButtonHandlerContainer;

public class ScreenButtonPacket
{
    private final int buttonID;
    @Nullable private final CompoundTag extraNBT;

    public ScreenButtonPacket(int buttonID, @Nullable CompoundTag extraNBT)
    {
        this.buttonID = buttonID;
        this.extraNBT = extraNBT;
    }

    ScreenButtonPacket(FriendlyByteBuf buffer)
    {
        buttonID = buffer.readVarInt();
        if (buffer.readBoolean())
        {
            extraNBT = buffer.readNbt();
        }
        else
        {
            extraNBT = null;
        }
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(buttonID);
        buffer.writeBoolean(extraNBT != null);
        if (extraNBT != null)
        {
            buffer.writeNbt(extraNBT);
        }
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null && sender.containerMenu instanceof ButtonHandlerContainer handler)
            {
                handler.onButtonPress(buttonID, extraNBT);
            }
        });
    }
}
