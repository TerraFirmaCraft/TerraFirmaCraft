package net.dries007.tfc.network;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import stanhebben.zenscript.util.ArrayUtil;

public class PacketSimpleMessage implements IMessage
{
    private boolean isLocalized;
    private String text;
    private String category;
    private TextFormatting color = TextFormatting.WHITE;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isStrikethrough = false;
    private boolean isUnderline = false;
    private boolean isObfuscated = false;

    public PacketSimpleMessage()
    {
    }

    public PacketSimpleMessage(String category, String unlocalized)
    {
        this(category, false, unlocalized);
    }

    public PacketSimpleMessage(String category, String unlocalized, TextFormatting... formats)
    {
        this(category, false, unlocalized, formats);
    }

    public PacketSimpleMessage(String category, boolean isLocalized, String text, TextFormatting... formats)
    {
        this.isLocalized = isLocalized;
        this.category = category;
        this.text = text;
        for (TextFormatting format : formats)
        {
            switch (format)
            {
                case BOLD:
                    isBold = true;
                    break;
                case ITALIC:
                    isItalic = true;
                    break;
                case OBFUSCATED:
                    isObfuscated = true;
                    break;
                case STRIKETHROUGH:
                    isStrikethrough = true;
                    break;
                case UNDERLINE:
                    isUnderline = true;
                    break;
                default:
                    if (format != null && format.isColor())
                    {
                        color = format;
                    }
                    break;
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        isLocalized = buf.readBoolean();
        category = buf.readCharSequence(buf.readInt(), Charset.defaultCharset()).toString();
        text = buf.readCharSequence(buf.readInt(), Charset.defaultCharset()).toString();
        isBold = buf.readBoolean();
        isItalic = buf.readBoolean();
        isObfuscated = buf.readBoolean();
        isStrikethrough = buf.readBoolean();
        isUnderline = buf.readBoolean();
        color = TextFormatting.fromColorIndex(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(isLocalized);
        buf.writeInt(category.length());
        buf.writeCharSequence(category, Charset.defaultCharset());
        buf.writeInt(text.length());
        buf.writeCharSequence(text, Charset.defaultCharset());
        buf.writeBoolean(isBold);
        buf.writeBoolean(isItalic);
        buf.writeBoolean(isObfuscated);
        buf.writeBoolean(isStrikethrough);
        buf.writeBoolean(isUnderline);
        buf.writeInt(color.getColorIndex());
    }

    public static final class Handler implements IMessageHandler<PacketSimpleMessage, IMessage>
    {

        @Override
        public IMessage onMessage(PacketSimpleMessage message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    ITextComponent text = message.isLocalized ? new TextComponentString(message.text) : new TextComponentTranslation(message.text);
                    Style style = text.getStyle();
                    style.setBold(message.isBold);
                    style.setItalic(message.isItalic);
                    style.setObfuscated(message.isObfuscated);
                    style.setStrikethrough(message.isStrikethrough);
                    style.setUnderlined(message.isUnderline);
                    style.setColor(message.color);
                    player.sendStatusMessage(text, ArrayUtil.contains(ConfigTFC.Client.TOOLTIP.tooltipCategoriesOutputToActionBar, message.category));
                }
            });
            return null;
        }

    }
}
