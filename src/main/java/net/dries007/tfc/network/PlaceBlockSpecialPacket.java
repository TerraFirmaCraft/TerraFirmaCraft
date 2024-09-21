/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.Optional;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.PlacedItemBlock;
import net.dries007.tfc.config.TFCConfig;

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
            if (rayTrace instanceof final BlockHitResult blockResult && blockResult.getDirection() == Direction.UP)
            {
                // We must be targeting the top face of a block, otherwise we deny placement
                final BlockPos pos = blockResult.getBlockPos();
                final BlockPos above = pos.above();
                final ItemStack stack = player.getMainHandItem();

                // First, check if we are targeting a placed item, or the bottom of a shelf (which can contain placed items). We ensure we only
                // catch the bottom shelf check here by ensuring our hit Y is below half of the block
                @Nullable PlacedItemBlockEntity placedItem = level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get())
                    .or(() -> blockResult.getLocation().y() - blockResult.getBlockPos().getY() < 0.5f
                        ? level.getBlockEntity(pos, TFCBlockEntities.SHELF.get())
                        : Optional.empty())
                    .orElse(null);
                if (placedItem != null)
                {
                    // If successful, then interact with the existing placed item
                    placedItem.onRightClick(player, stack, blockResult);
                }
                else if (!stack.isEmpty())
                {
                    // Otherwise, if we're not interacting with an existing placed item, we must be placing a new unsealedStack
                    // The block we clicked on must be a solid surface (which includes shelves), and the block above must be empty,
                    // or a shelf. First, check for a shelf which we can place beneath.
                    placedItem = level.getBlockEntity(above, TFCBlockEntities.SHELF.get())
                        .orElse(null);
                    if (placedItem != null)
                    {
                        placedItem.onRightClick(player, stack, blockResult);
                    }
                    else if (level.isEmptyBlock(above))
                    {
                        // Otherwise, try and place a block on top of the surface that we clicked.
                        final BlockState toPlace = PlacedItemBlock.updateStateValues(level, pos, TFCBlocks.PLACED_ITEM.get().defaultBlockState());
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
