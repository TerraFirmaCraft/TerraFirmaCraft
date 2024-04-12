/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.recipes.ChiselRecipe;

public class PlayerDataUpdatePacket
{
    private final long lastDrinkTick;
    private final long intoxicationTick;
    private final ChiselRecipe.Mode mode;


    public PlayerDataUpdatePacket(long lastDrinkTick, long intoxicationTick, ChiselRecipe.Mode mode)
    {
        this.lastDrinkTick = lastDrinkTick;
        this.intoxicationTick = intoxicationTick;
        this.mode = mode;
    }

    PlayerDataUpdatePacket(FriendlyByteBuf buffer)
    {
        this.lastDrinkTick = buffer.readVarLong();
        this.intoxicationTick = buffer.readVarLong();
        this.mode = ChiselRecipe.Mode.valueOf(buffer.readVarInt());
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
