/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;

public class PetCommandPacket
{
    private final int entityId;
    private final int command;

    public PetCommandPacket(Entity entityId, TamableMammal.Command command)
    {
        this.entityId = entityId.getId();
        this.command = command.ordinal();
    }

    PetCommandPacket(FriendlyByteBuf buffer)
    {
        this.entityId = buffer.readVarInt();
        this.command = buffer.readVarInt();
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
