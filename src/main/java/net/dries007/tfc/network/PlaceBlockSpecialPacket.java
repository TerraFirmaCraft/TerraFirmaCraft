/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.PlacedItemBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public enum PlaceBlockSpecialPacket implements CustomPacketPayload
{
    PACKET;

    public static final CustomPacketPayload.Type<PlaceBlockSpecialPacket> TYPE = PacketHandler.type("place_block");
    public static final StreamCodec<ByteBuf, PlaceBlockSpecialPacket> CODEC = StreamCodec.unit(PACKET);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && TFCConfig.SERVER.enablePlacingItems.get() && player.mayBuild())
        {
            final Level level = player.level();
            final HitResult rayTrace = player.pick(5.0F, 1.0F, false);
            if (rayTrace instanceof final BlockHitResult blockResult)
            {
                final Direction face = blockResult.getDirection();
                if (face == Direction.UP)
                {
                    final BlockPos pos = blockResult.getBlockPos();
                    final BlockPos above = pos.above();
                    final BlockState state = level.getBlockState(pos);
                    final ItemStack stack = player.getMainHandItem();
                    final Block placedItem = TFCBlocks.PLACED_ITEM.get();

                    if (Helpers.isBlock(state, placedItem))
                    {
                        level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get()).ifPresent(e -> e.onRightClick(player, stack, blockResult));
                    }
                    else if (!stack.isEmpty() && level.isEmptyBlock(above))
                    {
                        double y = blockResult.getLocation().y - pos.getY();
                        if (y == 0 || y == 1) // if we are on the top or bottom face
                        {
                            final BlockState toPlace = PlacedItemBlock.updateStateValues(level, pos, placedItem.defaultBlockState());
                            if (!PlacedItemBlock.isEmptyContents(toPlace))
                            {
                                level.setBlockAndUpdate(above, toPlace);
                                level.getBlockEntity(above, TFCBlockEntities.PLACED_ITEM.get()).ifPresent(e -> e.insertItem(player, stack, blockResult));
                            }
                        }
                    }
                }
            }
        }
    }
}
