/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record PetCommandPacket(
    int entityId,
    TamableMammal.Command command
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PetCommandPacket> TYPE = PacketHandler.type("pet_command");
    public static final StreamCodec<ByteBuf, PetCommandPacket> STREAM = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.entityId,
        TamableMammal.Command.STREAM, c -> c.command,
        PetCommandPacket::new
    );

    public PetCommandPacket(Entity entityId, TamableMammal.Command command)
    {
        this(entityId.getId(), command);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.serverLevel().getEntity(entityId) instanceof TamableMammal pet)
        {
            if (pet.willListenTo(command, false))
            {
                pet.receiveCommand(player, command);
            }
            else
            {
                player.displayClientMessage(Component.translatable("tfc.pet.will_not_listen"), true);
            }
        }
    }
}
