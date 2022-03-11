/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.PlacedItemBlock;
import net.dries007.tfc.util.Helpers;

public class PlaceBlockSpecialPacket
{
    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null)
            {
                final Level world = player.getLevel();
                final HitResult rayTrace = player.pick(5.0F, 1.0F, false);
                if (rayTrace instanceof final BlockHitResult blockResult)
                {
                    final Direction face = blockResult.getDirection();
                    if (face == Direction.UP)
                    {
                        final BlockPos pos = blockResult.getBlockPos();
                        final BlockPos above = pos.above();
                        final BlockState state = world.getBlockState(pos);
                        final ItemStack stack = player.getMainHandItem();
                        if (Helpers.isBlock(state, TFCBlocks.PLACED_ITEM.get()))
                        {
                            world.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get()).ifPresent(e -> e.onRightClick(player, stack, blockResult));
                        }
                        else if (!stack.isEmpty() && world.isEmptyBlock(above))
                        {
                            double y = blockResult.getLocation().y - pos.getY();
                            if (y == 0 || y == 1)
                            {
                                world.setBlockAndUpdate(above, PlacedItemBlock.updateStateValues(world, pos, TFCBlocks.PLACED_ITEM.get().defaultBlockState()));
                                world.getBlockEntity(above, TFCBlockEntities.PLACED_ITEM.get()).ifPresent(e -> e.insertItem(player, stack, blockResult));
                            }
                        }
                    }
                }
            }
        });
    }
}
