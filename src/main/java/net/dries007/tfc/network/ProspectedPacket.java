/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.items.ProspectResult;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.events.ProspectedEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;

public record ProspectedPacket(
    Block block,
    ProspectResult result
)
{
    ProspectedPacket(FriendlyByteBuf buffer)
    {
        this(
            BuiltInRegistries.BLOCK.byId(buffer.readVarInt()),
            ProspectResult.valueOf(buffer.readByte())
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(BuiltInRegistries.BLOCK.getId(block));
        buffer.writeByte(result.ordinal());
    }

    void handle()
    {
        final Player player = ClientHelpers.getPlayer();
        if (player != null)
        {
            MinecraftForge.EVENT_BUS.post(new ProspectedEvent(player, result, block));
            player.displayClientMessage(result.getText(block), TFCConfig.CLIENT.sendProspectResultsToActionbar.get());
        }
    }
}
