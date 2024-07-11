/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.Optional;

import net.dries007.tfc.common.container.ButtonHandlerContainer;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record ScreenButtonPacket(
    int buttonId,
    Optional<CompoundTag> extraNbt
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ScreenButtonPacket> TYPE = PacketHandler.type("screen_button");
    public static final StreamCodec<ByteBuf, ScreenButtonPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.buttonId,
        ByteBufCodecs.OPTIONAL_COMPOUND_TAG, c -> c.extraNbt,
        ScreenButtonPacket::new
    );

    public ScreenButtonPacket(int buttonId)
    {
        this(buttonId, Optional.empty());
    }

    public ScreenButtonPacket(int buttonId, CompoundTag extraNbt)
    {
        this(buttonId, Optional.of(extraNbt));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.containerMenu instanceof ButtonHandlerContainer handler)
        {
            handler.onButtonPress(buttonId, extraNbt.orElse(null));
        }
    }
}
