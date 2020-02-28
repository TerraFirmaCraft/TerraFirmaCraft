/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.api.recipes.ChiselRecipe;
import net.dries007.tfc.objects.items.metal.ItemMetalChisel;

public class PacketCycleItemMode implements IMessageEmpty
{
    public static final class Handler implements IMessageHandler<PacketCycleItemMode, IMessage>
    {
        @Override
        public IMessage onMessage(PacketCycleItemMode message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    if (player.getHeldItemMainhand().getItem() instanceof ItemMetalChisel)
                    {
                        IPlayerData capability = player.getCapability(CapabilityPlayerData.CAPABILITY, null);

                        if (capability != null)
                        {
                            ChiselRecipe.Mode mode = capability.getChiselMode();
                            capability.setChiselMode(mode.next());
                            capability.updateAndSync();
                        }
                    }

                }
            });
            return null;
        }
    }
}
