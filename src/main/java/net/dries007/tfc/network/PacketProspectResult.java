package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.events.ProspectEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketProspectResult implements IMessage
{
    private BlockPos pos;
    private ProspectEvent.ResultType type;
    private String ore;
    private double score;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketProspectResult() {}

    public PacketProspectResult(BlockPos pos, ProspectEvent.ResultType type, String ore, double score)
    {
        this.pos = pos;
        this.type = type;
        this.ore = ore;
        this.score = score;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.pos = new BlockPos(x, y, z);
        this.type = ProspectEvent.ResultType.values()[buf.readByte()];

        if (type == ProspectEvent.ResultType.NOTHING)
        {
            this.ore = null;
            this.score = 0.0D;
        }
        else
        {
            this.ore = ByteBufUtils.readUTF8String(buf);
            this.score = buf.readDouble();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeByte(type.ordinal());

        if (type != ProspectEvent.ResultType.NOTHING)
        {
            ByteBufUtils.writeUTF8String(buf, ore);
            buf.writeDouble(score);
        }
    }

    public static final class Handler implements IMessageHandler<PacketProspectResult, IMessage>
    {
        @Override
        public IMessage onMessage(PacketProspectResult message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    ITextComponent text = new TextComponentTranslation(message.type.translation);
                    if (message.type != ProspectEvent.ResultType.NOTHING)
                    {
                        text.appendText(" ").appendSibling(new TextComponentTranslation(message.ore + ".name"));
                    }
                    player.sendStatusMessage(text, ConfigTFC.Client.TOOLTIP.propickOutputToActionBar);
                }

                ProspectEvent event = new ProspectEvent.Client(player, message.pos, message.type, message.ore, message.score);
                MinecraftForge.EVENT_BUS.post(event);
            });
            return null;
        }
    }
}
