/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.compat.patchouli.PatchouliIntegration;

public class OpenFieldGuidePacket
{
    private final ResourceLocation id;
    private final int page;

    public OpenFieldGuidePacket(ResourceLocation id, int page)
    {
        this.id = id;
        this.page = page;
    }

    OpenFieldGuidePacket(FriendlyByteBuf buffer)
    {
        this.id = buffer.readResourceLocation();
        this.page = buffer.readVarInt();
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(id);
        buffer.writeVarInt(page);
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null)
            {
                player.doCloseContainer();
                PatchouliIntegration.openGui(player, id, page);
            }
        });
    }
}
