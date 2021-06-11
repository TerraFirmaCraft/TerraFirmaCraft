/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import java.nio.charset.Charset;
import java.util.function.BooleanSupplier;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSimpleMessage implements IMessage
{
    public enum MessageCategory
    {
        ANVIL(() -> ConfigTFC.Client.TOOLTIP.anvilWeldOutputToActionBar),
        VESSEL(() -> ConfigTFC.Client.TOOLTIP.vesselOutputToActionBar),
        ANIMAL(() -> ConfigTFC.Client.TOOLTIP.animalsOutputToActionBar);

        private final BooleanSupplier displayToToolbar;

        MessageCategory(BooleanSupplier displayToToolbar)
        {
            this.displayToToolbar = displayToToolbar;
        }
    }

    /**
     * Utility method for making a message with just a single {@link TextComponentTranslation} element.
     */
    public static PacketSimpleMessage translateMessage(MessageCategory category, String unlocalized, Object... args)
    {
        return new PacketSimpleMessage(category, new TextComponentTranslation(unlocalized, args));
    }

    /**
     * Utility method for making a message with just a single {@link TextComponentString} element.
     */
    public static PacketSimpleMessage stringMessage(MessageCategory category, String localized)
    {
        return new PacketSimpleMessage(category, new TextComponentString(localized));
    }

    private ITextComponent text;
    private MessageCategory category;

    public PacketSimpleMessage()
    {
    }

    public PacketSimpleMessage(MessageCategory category, ITextComponent text)
    {
        this.text = text;
        this.category = category;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        category = MessageCategory.values()[buf.readInt()];
        text = ITextComponent.Serializer.jsonToComponent(buf.readCharSequence(buf.readInt(), Charset.defaultCharset()).toString());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(category.ordinal());
        String json = ITextComponent.Serializer.componentToJson(text);
        buf.writeInt(json.length());
        buf.writeCharSequence(json, Charset.defaultCharset());
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
                    player.sendStatusMessage(message.text, message.category.displayToToolbar.getAsBoolean());
                }
            });
            return null;
        }

    }
}
