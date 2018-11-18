/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.objects.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.objects.te.TEAnvilTFC;

public class PacketAnvilRecipe implements IMessage
{
    private BlockPos pos;
    private AnvilRecipe recipe;

    // no args constructor required for forge
    @SuppressWarnings("unused")
    public PacketAnvilRecipe() {}

    public PacketAnvilRecipe(TEAnvilTFC tile)
    {
        this.pos = tile.getPos();
        this.recipe = tile.getRecipe();
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        pos = BlockPos.fromLong(buffer.readLong());
        boolean isNotNull = buffer.readBoolean();
        if (isNotNull)
            recipe = AnvilRecipe.deserialize(buffer);
        else
            recipe = null;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeLong(pos.toLong());
        buffer.writeBoolean(recipe != null);
        if (recipe != null)
            AnvilRecipe.serialize(recipe, buffer);
    }

    public static class Handler implements IMessageHandler<PacketAnvilRecipe, IMessage>
    {
        @Override
        public IMessage onMessage(PacketAnvilRecipe message, MessageContext ctx)
        {
            return null;
        }
    }
}
