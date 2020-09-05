/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.particle.TFCParticles;

public class PacketSpawnTFCParticle implements IMessage
{
    private int particleID;
    private double x, y, z;
    private double speedX, speedY, speedZ;
    private int duration;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketSpawnTFCParticle()
    {
    }

    public PacketSpawnTFCParticle(TFCParticles particle, double x, double y, double z, double speedX, double speedY, double speedZ, int duration)
    {
        this.particleID = particle.ordinal();
        this.x = x;
        this.y = y;
        this.z = z;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.duration = duration;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.particleID = buffer.readInt();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.speedX = buffer.readDouble();
        this.speedY = buffer.readDouble();
        this.speedZ = buffer.readDouble();
        this.duration = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        byteBuf.writeInt(particleID);
        byteBuf.writeDouble(x);
        byteBuf.writeDouble(y);
        byteBuf.writeDouble(z);
        byteBuf.writeDouble(speedX);
        byteBuf.writeDouble(speedY);
        byteBuf.writeDouble(speedZ);
        byteBuf.writeInt(duration);
    }

    public static class Handler implements IMessageHandler<PacketSpawnTFCParticle, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSpawnTFCParticle message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    TFCParticles particle = TFCParticles.values()[message.particleID];
                    particle.spawn(player.getEntityWorld(), message.x, message.y, message.z, message.speedX, message.speedY, message.speedZ, message.duration);
                }
            });
            return null;
        }
    }
}
