/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
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

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            Player player = ClientHelpers.getPlayer();
            if (player != null)
            {
                player.getCapability(PlayerDataCapability.CAPABILITY).ifPresent(p -> p.updateFromPacket(lastDrinkTick, intoxicationTick, mode));
            }
        });
    }
}
