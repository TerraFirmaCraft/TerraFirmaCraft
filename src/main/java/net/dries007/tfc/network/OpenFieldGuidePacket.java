/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.compat.patchouli.PatchouliIntegration;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record OpenFieldGuidePacket(
    ResourceLocation id,
    int page
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<OpenFieldGuidePacket> TYPE = PacketHandler.type("open_field_guide");
    public static final StreamCodec<ByteBuf, OpenFieldGuidePacket> STREAM = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, c -> c.id,
        ByteBufCodecs.VAR_INT, c -> c.page,
        OpenFieldGuidePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            player.doCloseContainer();
            PatchouliIntegration.openGui(player, id, page);
        }
    }
}
