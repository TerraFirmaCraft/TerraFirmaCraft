package net.dries007.tfc.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import net.dries007.tfc.ForgeEventHandler;

/**
 * Sent to the server when the player clicks a location that might trigger a drinking.
 * Since the server doesn't know about these naturally, we have to check and sync them ourselves.
 *
 * @see net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty
 */
public class PlayerDrinkPacket
{
    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null)
            {
                final InteractionResult result = ForgeEventHandler.attemptDrink(player.level, player, true);
                if (result.shouldSwing())
                {
                    player.swing(InteractionHand.MAIN_HAND, true);
                }
            }
        });
    }
}
