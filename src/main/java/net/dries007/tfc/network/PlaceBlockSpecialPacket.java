package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.common.blocks.PlacedItemBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.PlacedItemTileEntity;
import net.dries007.tfc.util.Helpers;

public class PlaceBlockSpecialPacket
{
    public PlaceBlockSpecialPacket()
    {

    }

    PlaceBlockSpecialPacket(PacketBuffer buffer)
    {

    }

    void encode(PacketBuffer buffer)
    {

    }

    void handle(Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();

        ServerPlayerEntity player = context.getSender();
        if (player != null)
        {
            World world = player.getLevel();
            PlayerController mode = Minecraft.getInstance().gameMode;
            if (mode != null)
            {
                RayTraceResult rayTrace = player.pick(mode.getPickRange(), 1.0F, false);
                if (rayTrace instanceof BlockRayTraceResult)
                {
                    BlockRayTraceResult blockResult = (BlockRayTraceResult) rayTrace;
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
                                PlacedItemTileEntity te = Helpers.getTileEntityOrThrow(world, pos.above(), PlacedItemTileEntity.class);
                                te.insertItem(player, stack, blockResult);
                            }
                        }
                    }
                }
            }
        }
    }
}
