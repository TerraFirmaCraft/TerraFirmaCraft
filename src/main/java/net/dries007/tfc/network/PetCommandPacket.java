package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.network.NetworkEvent;

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

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final ServerPlayer sender = context.getSender();
            if (sender != null)
            {
                Entity entity = sender.level.getEntity(entityId);
                if (entity instanceof TamableMammal pet)
                {
                    pet.receiveCommand(sender, TamableMammal.Command.valueOf(command));
                }
            }
        });
    }

}
