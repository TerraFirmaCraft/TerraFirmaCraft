/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.container.ContainerLargeVesselFluid;
import net.dries007.tfc.objects.container.ContainerLargeVesselSolid;
import net.dries007.tfc.objects.te.TELargeVessel;

public class PacketSwitchLargeVesselModeTab implements IMessage
{
    private BlockPos pos;
    private TFCGuiHandler.Type typeToSwitchTo;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketSwitchLargeVesselModeTab() {}

    public PacketSwitchLargeVesselModeTab(TELargeVessel tile, TFCGuiHandler.Type typeToSwitchTo)
    {
        this.pos = tile.getPos();
        this.typeToSwitchTo = typeToSwitchTo;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        typeToSwitchTo = TFCGuiHandler.Type.valueOf(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(typeToSwitchTo.ordinal());
    }

    public static final class Handler implements IMessageHandler<PacketSwitchLargeVesselModeTab, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSwitchLargeVesselModeTab message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    boolean isInPos = false;
                    if (player.openContainer instanceof ContainerLargeVesselFluid)
                    {
                        if (((ContainerLargeVesselFluid) player.openContainer).getTilePos().equals(message.pos))
                        {
                            isInPos = true;
                        }
                    }
                    else if (player.openContainer instanceof ContainerLargeVesselSolid)
                    {
                        if (((ContainerLargeVesselSolid) player.openContainer).getTilePos().equals(message.pos))
                        {
                            isInPos = true;
                        }
                    }

                    if (isInPos)
                    {
                        if (message.typeToSwitchTo == TFCGuiHandler.Type.LARGE_VESSEL_FLUID)
                        {
                            if (!(player.openContainer instanceof ContainerLargeVesselFluid))
                            {
                                player.openContainer.onContainerClosed(player);
                                TFCGuiHandler.openGui(world, message.pos, player, message.typeToSwitchTo);
                            }
                        }
                        else if (message.typeToSwitchTo == TFCGuiHandler.Type.LARGE_VESSEL_SOLID)
                        {
                            if (!(player.openContainer instanceof ContainerLargeVesselSolid))
                            {
                                player.openContainer.onContainerClosed(player);
                                TFCGuiHandler.openGui(world, message.pos, player, message.typeToSwitchTo);
                            }
                        }
                    }
                });
            }
            return null;
        }
    }
}
