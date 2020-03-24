/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui.overlay;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.items.metal.ItemMetalChisel;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Interfacing to pass on DrawHighlightEvent's custom implementations
 */
public interface IHighlightHandler
{
    /**
     * Returns an AxisAlignedBB obj containing a full box to the location where player is looking at
     *
     * @param player       the player
     * @param pos          the blockpos to offset this AxisAlignedBB to
     * @param partialTicks current frame's partial ticks (since FPS is higher than TPS)
     * @return an AxisAlignedBB containing a full block's box
     */
    static AxisAlignedBB getBox(EntityPlayer player, BlockPos pos, double partialTicks)
    {
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        return new AxisAlignedBB(pos).offset(-dx, -dy, -dz);
    }

    /**
     * Draws the outlining of an AxisAlignedBB obj
     *
     * @param box       AxisAlignedBB to draw
     * @param lineWidth where 1 = MC's default selection box
     * @param red       [0-1] red value
     * @param green     [0-1] green value
     * @param blue      [0-1] blue value
     * @param alpha     [0-1] alpha value
     */
    static void drawBox(AxisAlignedBB box, float lineWidth, float red, float green, float blue, float alpha)
    {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(lineWidth);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        RenderGlobal.drawSelectionBoundingBox(box, red, green, blue, alpha);

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Handles drawing custom bounding boxes depending on where player is looking at
     *
     * @param world        the client's world obj
     * @param pos          the blockpos player is looking at
     * @param player       the player that is looking at this block
     * @param rayTrace     the RayTraceResult, got from DrawBlockHighlightEvent
     * @param partialTicks current frame's partial ticks (since FPS is higher than TPS)
     * @return true if you wish to cancel drawing the block's bounding box outline
     */
    boolean drawHighlight(World world, BlockPos pos, EntityPlayer player, RayTraceResult rayTrace, double partialTicks);

    @SideOnly(Side.CLIENT)
    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    final class EventHandler
    {
        /**
         * Handles custom bounding boxes drawing
         * eg: Chisel, Quern handle
         */
        @SubscribeEvent
        public static void drawHighlightEvent(DrawBlockHighlightEvent event)
        {
            final EntityPlayer player = event.getPlayer();
            final World world = player.getEntityWorld();
            final RayTraceResult traceResult = event.getTarget();
            final BlockPos lookingAt = traceResult.getBlockPos();

            //noinspection ConstantConditions
            if (lookingAt != null)
            {
                // Handle Chisel first
                if (event.getPlayer().getHeldItemMainhand().getItem() instanceof ItemMetalChisel)
                {
                    // Get the state that the chisel would turn the block into if it clicked
                    IBlockState newState = ItemMetalChisel.getChiselResultState(player, player.world, lookingAt, traceResult.sideHit, (float) traceResult.hitVec.x - lookingAt.getX(), (float) traceResult.hitVec.y - lookingAt.getY(), (float) traceResult.hitVec.z - lookingAt.getZ());
                    if (newState != null)
                    {
                        AxisAlignedBB box = IHighlightHandler.getBox(player, lookingAt, event.getPartialTicks()).grow(0.001);
                        double offsetX = 0, offsetY = 0, offsetZ = 0;

                        if (newState.getBlock() instanceof BlockStairs)
                        {
                            EnumFacing facing = newState.getValue(BlockStairs.FACING);

                            offsetY = (newState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) ? -0.5 : 0.5;
                            offsetX = -facing.getXOffset() * 0.5;
                            offsetZ = -facing.getZOffset() * 0.5;
                        }
                        else if (newState.getBlock() instanceof BlockSlab)
                        {
                            offsetY = (newState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) ? -0.5 : 0.5;
                        }

                        box = box.intersect(box.offset(offsetX, offsetY, offsetZ));

                        IHighlightHandler.drawBox(box, 5f, 1, 0, 0, 0.8f);
                    }
                }
                else if (world.getBlockState(lookingAt).getBlock() instanceof IHighlightHandler)
                {
                    // Pass on to custom implementations
                    IHighlightHandler handler = (IHighlightHandler) world.getBlockState(lookingAt).getBlock();
                    if (handler.drawHighlight(world, lookingAt, player, traceResult, event.getPartialTicks()))
                    {
                        // Cancel drawing this block's bounding box
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
