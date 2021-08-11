/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.fmllegacy.network.NetworkEvent;

import net.dries007.tfc.common.container.IButtonHandler;

public class ScreenButtonPacket
{
    private final int buttonID;
    @Nullable private final CompoundTag extraNBT;

    public ScreenButtonPacket(FriendlyByteBuf buffer)
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

    public ScreenButtonPacket(int buttonID, @Nullable CompoundTag extraNBT)
    {
        this.buttonID = buttonID;
        this.extraNBT = extraNBT;
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

    void handle(Supplier<NetworkEvent.Context> contextSupplier)
    {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender != null && sender.containerMenu instanceof IButtonHandler buttonHandler)
            {
                buttonHandler.onButtonPress(buttonID, extraNBT);
            }
        });
    }
}
