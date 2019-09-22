package net.dries007.tfc.client.render;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.objects.items.metal.ItemMetalChisel;

public class ChiselHighlightHandler
{
    @SubscribeEvent
    public static void drawBlockHighlightEvent(DrawBlockHighlightEvent event)
    {
        // check if there is a chisel in the player's hand
        // if no chisel, don't render anything.
        if (!(event.getPlayer().getHeldItemMainhand().getItem() instanceof ItemMetalChisel))
            return;

        // Get the state that the chisel would turn the block into if it clicked
        /*
        IBlockState newState = ItemMetalChisel.getChiselResultState(
            event.getPlayer(),
            event.getPlayer().world,
            event.getTarget().getBlockPos(),
            event.getTarget().sideHit,
            (float) event.getTarget().hitVec.x,
            (float) event.getTarget().hitVec.y,
            (float) event.getTarget().hitVec.z);

        if (newState instanceof BlockStairs)
        {
            // the chisel would turn it into a stair, check the stair properties
        }
        else if (newState instanceof BlockSlab)
        {

        }
        */
    }

    private static void drawBox(BlockPos pos, Box box)
    {

    }

    private static class Box
    {
        private double posX, posY, posZ;
        private double widthX, widthY, widthZ;


    }

}
