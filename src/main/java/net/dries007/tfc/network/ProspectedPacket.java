/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.items.ProspectResult;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.events.ProspectedEvent;

public class ProspectedPacket
{
    private final Block block;
    private final ProspectResult result;

    public ProspectedPacket(Block block, ProspectResult result)
    {
        this.block = block;
        this.result = result;
    }

    ProspectedPacket(FriendlyByteBuf buffer)
    {
        this.block = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
        this.result = ProspectResult.valueOf(buffer.readByte());
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
        buffer.writeByte(result.ordinal());
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final Player player = ClientHelpers.getPlayer();
            if (player != null)
            {
                MinecraftForge.EVENT_BUS.post(new ProspectedEvent(player, result, block));
                player.displayClientMessage(result.getText(block), TFCConfig.CLIENT.sendProspectResultsToActionbar.get());
            }
        });
    }
}
