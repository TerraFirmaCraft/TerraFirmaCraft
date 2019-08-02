/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPlacedItem;
import net.dries007.tfc.util.Helpers;

/**
 * This packet is send when the client player presses the "Place Block Special" keybind. It has no special information
 */
public class PacketPlaceBlockSpecial implements IMessageEmpty
{
    public static final class Handler implements IMessageHandler<PacketPlaceBlockSpecial, IMessage>
    {
        @Override
        public IMessage onMessage(PacketPlaceBlockSpecial message, MessageContext ctx)
        {
            final EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {

                    final World world = player.getEntityWorld();
                    final RayTraceResult rayTrace = Helpers.rayTrace(player, player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue(), 1);
                    final ItemStack stack = player.getHeldItemMainhand().isEmpty() ? player.getHeldItemOffhand() : player.getHeldItemMainhand();

                    if (rayTrace != null)
                    {
                        BlockPos pos = rayTrace.getBlockPos();
                        EnumFacing hitFace = rayTrace.sideHit;
                        double placeReach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                        if (player.getDistanceSq(pos) <= placeReach * placeReach && hitFace != null)
                        {
                            if (!stack.isEmpty() && world.getBlockState(pos).isNormalCube() && world.getBlockState(pos.offset(hitFace)).getBlock().isReplaceable(world, pos))
                            {
                                world.setBlockState(pos.up(), BlocksTFC.PLACED_ITEM.getDefaultState());
                                TEPlacedItem tile = Helpers.getTE(world, pos.up(), TEPlacedItem.class);
                                if (tile != null)
                                {
                                    tile.onRightClick(player, stack, rayTrace);
                                }
                            }
                            else if (world.getBlockState(pos).getBlock() == BlocksTFC.PLACED_ITEM)
                            {
                                TEPlacedItem tile = Helpers.getTE(world, pos, TEPlacedItem.class);
                                if (tile != null)
                                {
                                    tile.onRightClick(player, stack, rayTrace);
                                }
                            }
                            else if (world.getBlockState(pos.offset(hitFace)).getBlock() == BlocksTFC.PLACED_ITEM)
                            {
                                TEPlacedItem tile = Helpers.getTE(world, pos.offset(hitFace), TEPlacedItem.class);
                                if (tile != null)
                                {
                                    tile.onRightClick(player, stack, rayTrace);
                                }
                            }
                        }
                    }
                });
            }
            return null;
        }
    }
}

