/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record PlayerDataUpdatePacket(
    long lastDrinkTick,
    long intoxicationTick,
    ChiselRecipe.Mode mode
)
{
    PlayerDataUpdatePacket(FriendlyByteBuf buffer)
    {
        this(
            buffer.readVarLong(),
            buffer.readVarLong(),
            ChiselRecipe.Mode.valueOf(buffer.readVarInt())
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarLong(lastDrinkTick);
        buffer.writeVarLong(intoxicationTick);
        buffer.writeVarInt(mode.ordinal());
    }

    void handle()
    {
        final Player player = ClientHelpers.getPlayer();
        if (player != null)
        {
            PlayerData.get(player).updateFromPacket(lastDrinkTick, intoxicationTick, mode);
        }
    }
}
