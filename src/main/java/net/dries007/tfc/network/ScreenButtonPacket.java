/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.container.ButtonHandlerContainer;
import net.dries007.tfc.util.Helpers;

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
        extraNBT = Helpers.decodeNullable(buffer, FriendlyByteBuf::readNbt);
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(buttonID);
        Helpers.encodeNullable(extraNBT, buffer, (nbt, buf) -> buf.writeNbt(nbt));
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.containerMenu instanceof ButtonHandlerContainer handler)
        {
            handler.onButtonPress(buttonID, extraNBT);
        }
    }
}
