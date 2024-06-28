/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.container.ButtonHandlerContainer;
import net.dries007.tfc.util.Helpers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record ScreenButtonPacket(
    int buttonId,
    @Nullable CompoundTag extraNbt
)
{
    ScreenButtonPacket(FriendlyByteBuf buffer)
    {
        this(
            buffer.readVarInt(),
            Helpers.decodeNullable(buffer, FriendlyByteBuf::readNbt)
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(buttonId);
        Helpers.encodeNullable(extraNbt, buffer, (nbt, buf) -> buf.writeNbt(nbt));
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.containerMenu instanceof ButtonHandlerContainer handler)
        {
            handler.onButtonPress(buttonId, extraNbt);
        }
    }
}
