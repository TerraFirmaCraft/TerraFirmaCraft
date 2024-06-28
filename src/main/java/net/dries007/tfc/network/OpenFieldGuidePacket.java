/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record OpenFieldGuidePacket(
    ResourceLocation id,
    int page
)
{
    OpenFieldGuidePacket(FriendlyByteBuf buffer)
    {
        this(
            buffer.readResourceLocation(),
            buffer.readVarInt()
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(id);
        buffer.writeVarInt(page);
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
