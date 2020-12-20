package net.dries007.tfc.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.te.TEInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBlockInventoryUpdate implements IMessage
{
    private BlockPos pos;
    private Map<Integer, ItemStack> changes = new HashMap<>();

    public PacketBlockInventoryUpdate()
    {
    }

    public PacketBlockInventoryUpdate(BlockPos pos)
    {
        this.pos = pos;
    }

    public PacketBlockInventoryUpdate(BlockPos pos, ItemStack item, int slot)
    {
        this(pos);
        changes.put(slot, item);
    }

    public PacketBlockInventoryUpdate addChange(ItemStack item, int slot)
    {
        changes.put(slot, item);
        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        int entryCount = buf.readInt();
        for (int i = 0; i < entryCount; i++)
        {
            ItemStack item = ByteBufUtils.readItemStack(buf);
            int slot = buf.readInt();
            changes.put(slot, item);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(changes.size());
        for (Entry<Integer, ItemStack> entry : changes.entrySet())
        {
            ByteBufUtils.writeItemStack(buf, entry.getValue());
            buf.writeInt(entry.getKey());
        }
    }

    public static final class Handler implements IMessageHandler<PacketBlockInventoryUpdate, IMessage>
    {

        @Override
        public IMessage onMessage(PacketBlockInventoryUpdate message, MessageContext ctx)
        {
            final EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    World world = player.getEntityWorld();
                    TileEntity te = world.getTileEntity(message.pos);
                    if (te instanceof IInventory && !message.changes.isEmpty())
                    {
                        // Fill the slots with the appropriate items
                        IInventory inventory = (IInventory) te;
                        for (Entry<Integer, ItemStack> entry : message.changes.entrySet())
                        {
                            inventory.setInventorySlotContents(entry.getKey(), entry.getValue());
                        }
                    }
                });
            }
            return null;
        }

    }
}
