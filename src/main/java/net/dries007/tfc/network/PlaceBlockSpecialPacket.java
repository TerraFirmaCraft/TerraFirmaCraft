/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

import net.minecraftforge.fmllegacy.network.NetworkEvent;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.PlacedItemBlock;
import net.dries007.tfc.common.tileentity.PlacedItemTileEntity;
import net.dries007.tfc.common.tileentity.TFCTileEntities;
import net.dries007.tfc.util.Helpers;

public class PlaceBlockSpecialPacket
{
    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().setPacketHandled(true);
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null)
            {
                Level world = player.getLevel();
                HitResult rayTrace = player.pick(5.0F, 1.0F, false);
                if (rayTrace instanceof BlockHitResult blockResult)
            {
                    Direction face = blockResult.getDirection();
                    if (face == Direction.UP)
                    {
                        BlockPos pos = blockResult.getBlockPos();
                        BlockState state = world.getBlockState(pos);
                        ItemStack stack = player.getMainHandItem();
                        if (state.is(TFCBlocks.PLACED_ITEM.get()))
                        {
                            PlacedItemTileEntity te = Helpers.getTileEntity(world, pos, PlacedItemTileEntity.class);
                            if (te != null)
                            {
                                te.onRightClick(player, stack, blockResult);
                            }
                        }
                        else if (!stack.isEmpty() && world.isEmptyBlock(pos.above()))
                        {
                            double y = blockResult.getLocation().y - pos.getY();
                            if (y == 0 || y == 1)
                            {
                                world.setBlockAndUpdate(pos.above(), PlacedItemBlock.updateStateValues(world, pos, TFCBlocks.PLACED_ITEM.get().defaultBlockState()));
                                world.getBlockEntity(pos.above(), TFCTileEntities.PLACED_ITEM.get()).ifPresent(entity -> entity.insertItem(player, stack, blockResult));
                            }
                        }
                    }
                }
            }
        });
    }
}
