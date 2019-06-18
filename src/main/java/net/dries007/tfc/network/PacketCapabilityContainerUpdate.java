/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.nutrient.CapabilityFood;
import net.dries007.tfc.api.capability.nutrient.IFood;
import net.dries007.tfc.objects.container.CapabilityContainerListener;

/**
 * This is a packet which is sent to the client to sync capability data
 * It is used by {@link CapabilityContainerListener}
 *
 * @author Choonster
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
public class PacketCapabilityContainerUpdate implements IMessage
{
    private final TIntObjectMap<NBTTagCompound> capabilityData = new TIntObjectHashMap<>();
    private int windowID;

    @SuppressWarnings("unused")
    public PacketCapabilityContainerUpdate()
    {
    }

    public PacketCapabilityContainerUpdate(int windowID, int slotID, ItemStack stack)
    {
        this.windowID = windowID;

        final NBTTagCompound data = readCapability(stack);
        if (!data.isEmpty())
        {
            capabilityData.put(slotID, data);
        }
    }

    public PacketCapabilityContainerUpdate(int windowID, NonNullList<ItemStack> items)
    {
        this.windowID = windowID;

        for (int i = 0; i < items.size(); i++)
        {
            final NBTTagCompound nbt = readCapability(items.get(i));
            if (!nbt.isEmpty())
            {
                capabilityData.put(i, nbt);
            }
        }
    }

    @Override
    public final void fromBytes(final ByteBuf buf)
    {
        windowID = buf.readInt();

        final int numEntries = buf.readInt();
        for (int i = 0; i < numEntries; i++)
        {
            final int index = buf.readInt();
            final NBTTagCompound data = ByteBufUtils.readTag(buf);
            capabilityData.put(index, data);
        }
    }

    @Override
    public final void toBytes(final ByteBuf buf)
    {
        buf.writeInt(windowID);

        buf.writeInt(capabilityData.size());
        capabilityData.forEachEntry((index, data) -> {
            buf.writeInt(index);
            ByteBufUtils.writeTag(buf, data);
            return true;
        });
    }

    public final boolean hasData()
    {
        return !capabilityData.isEmpty();
    }

    @Nonnull
    private NBTTagCompound readCapability(final ItemStack stack)
    {
        IItemHeat itemHeat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        IFood food = stack.getCapability(CapabilityFood.CAPABILITY_NUTRIENTS, null);
        NBTTagCompound nbt = new NBTTagCompound();
        if (itemHeat != null)
        {
            nbt.setTag("heat", itemHeat.serializeNBT());
        }
        if (food != null)
        {
            nbt.setTag("food", food.serializeNBT());
        }
        return nbt;
    }

    @ParametersAreNonnullByDefault
    public static class Handler implements IMessageHandler<PacketCapabilityContainerUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(final PacketCapabilityContainerUpdate message, final MessageContext ctx)
        {
            if (!message.hasData()) return null; // Don't do anything if no data was sent

            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                final EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                final Container container;

                if (player != null)
                {
                    if (message.windowID == 0)
                    {
                        container = player.inventoryContainer;
                    }
                    else if (message.windowID == player.openContainer.windowId)
                    {
                        container = player.openContainer;
                    }
                    else
                    {
                        return;
                    }

                    message.capabilityData.forEachEntry((index, nbt) -> {
                        final ItemStack stack = container.getSlot(index).getStack();

                        final IItemHeat heat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                        if (heat != null)
                        {
                            NBTTagCompound heatNBT = nbt.getCompoundTag("heat");
                            if (!heat.serializeNBT().equals(heatNBT))
                            {
                                heat.deserializeNBT(heatNBT);
                            }
                        }

                        final IFood food = stack.getCapability(CapabilityFood.CAPABILITY_NUTRIENTS, null);
                        if (food != null)
                        {
                            NBTTagCompound foodNBT = nbt.getCompoundTag("food");
                            if (!food.serializeNBT().equals(foodNBT))
                            {
                                food.deserializeNBT(foodNBT);
                            }
                        }
                        return true;
                    });
                }
            });

            return null;
        }
    }
}
