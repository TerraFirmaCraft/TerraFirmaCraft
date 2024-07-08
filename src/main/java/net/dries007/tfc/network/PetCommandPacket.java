/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record PetCommandPacket(
    int entityId,
    int command
)
{
    public PetCommandPacket(Entity entityId, TamableMammal.Command command)
    {
        this(entityId.getId(), command.ordinal());
    }

    PetCommandPacket(FriendlyByteBuf buffer)
    {
        this(
            buffer.readVarInt(),
            buffer.readVarInt()
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(entityId);
        buffer.writeVarInt(command);
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            Entity entity = player.serverLevel().getEntity(entityId);
            if (entity instanceof TamableMammal pet)
            {
                final TamableMammal.Command value = TamableMammal.Command.valueOf(command);
                if (pet.willListenTo(value, false))
                {
                    pet.receiveCommand(player, value);
                }
                else
                {
                    player.displayClientMessage(Component.translatable("tfc.pet.will_not_listen"), true);
                }
            }
        }
    }
}
