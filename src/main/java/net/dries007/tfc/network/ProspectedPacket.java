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

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;

public record ProspectedPacket(
    Block block,
    ProspectResult result
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ProspectedPacket> TYPE = PacketHandler.type("prospected");
    public static final StreamCodec<RegistryFriendlyByteBuf, ProspectedPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.BLOCK), c -> c.block,
        ProspectResult.STREAM, c -> c.result,
        ProspectedPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        final Player player = ClientHelpers.getPlayer();
        if (player != null)
        {
            NeoForge.EVENT_BUS.post(new ProspectedEvent(player, result, block));
            player.displayClientMessage(result.getText(block), TFCConfig.CLIENT.sendProspectResultsToActionbar.get());
        }
    }
}
