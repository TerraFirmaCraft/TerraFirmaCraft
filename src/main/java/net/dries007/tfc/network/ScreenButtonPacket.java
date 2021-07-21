/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.common.container.IButtonHandler;

public class ScreenButtonPacket
{
    private final int buttonID;
    private final CompoundNBT extraNBT;

    public ScreenButtonPacket(PacketBuffer buffer)
    {
        buttonID = buffer.readInt();
        if (buffer.readBoolean())
        {
            extraNBT = buffer.readNbt();
        }
        else
        {
            extraNBT = null;
        }
    }

    public ScreenButtonPacket(int buttonID, @Nullable CompoundNBT extraNBT)
    {
        this.buttonID = buttonID;
        this.extraNBT = extraNBT;
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeInt(buttonID);
        buffer.writeBoolean(extraNBT != null);
        if (extraNBT != null)
        {
            buffer.writeNbt(extraNBT);
        }
    }

    void handle(Supplier<NetworkEvent.Context> contextSupplier)
    {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayerEntity sender = contextSupplier.get().getSender();
            if (sender != null && sender.containerMenu instanceof IButtonHandler)
            {
                ((IButtonHandler) sender.containerMenu).onButtonPress(buttonID, extraNBT);
            }
        });
    }
}
