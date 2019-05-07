package net.dries007.tfc.network;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;

/**
 * Packet to handle key press -> block place at location of the cursor.
 *
 * @author Claycorp
 */

public class PacketPlaceBlockFromKeyBind implements IMessage
{
    private BlockPos blockPos;
    private String block;

    @SuppressWarnings("unused")
    public PacketPlaceBlockFromKeyBind()
    {
    }

    /**
     * Packet to handle key press -> block place at location of the cursor.
     *
     * @param blockposin Blockpos the block will be placed at.
     *                   NOTE: All spacial transformations need to be done at the source as this is a direct pass through.
     *                   It also checks for air below, if the block is within real player limits and if the spot where the
     *                   block will be placed is replaceable.
     * @param blockin    the block to be placed as a Block Object.
     */
    public PacketPlaceBlockFromKeyBind(BlockPos blockposin, Block blockin)
    {
        this.blockPos = blockposin;
        this.block = Objects.requireNonNull(blockin.getRegistryName()).toString();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        blockPos = BlockPos.fromLong(buf.readLong());
        block = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(blockPos.toLong());
        ByteBufUtils.writeUTF8String(buf, block);
    }

    public static class Handler implements IMessageHandler<PacketPlaceBlockFromKeyBind, IMessage>
    {
        @Override
        public IMessage onMessage(PacketPlaceBlockFromKeyBind message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            World world = player.getEntityWorld();
            BlockPos blockPos = message.blockPos;
            Block block = Block.getBlockFromName(message.block);

            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                //sanity checks on block to stop the stupid.
                if (block != null)
                {
                    //more sanity checks to stop people from placing beyond what they should be, placing in thin air and make sure they don't outright replace something that shouldn't be.
                    if (player.getDistanceSq(blockPos) <= player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() &&
                        world.getBlockState(blockPos.down(1)) != Blocks.AIR.getDefaultState() && world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
                    {
                        //Place block.
                        world.setBlockState(blockPos, block.getDefaultState());
                    }
                }
            });
            return null;
        }
    }
}

