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
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.items.metal.ItemMetalChisel;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public class ChiselHighlightHandler
{
    @SubscribeEvent
    public static void drawBlockHighlightEvent(DrawBlockHighlightEvent event)
    {
        // check if there is a chisel in the player's hand. if no chisel, don't render anything.
        if (event.getPlayer().getHeldItemMainhand().getItem() instanceof ItemMetalChisel)
        {
            EntityPlayer player = event.getPlayer();
            RayTraceResult traceResult = event.getTarget();
            BlockPos pos = traceResult.getBlockPos();

            // pos is null if the raytraceresult is for an entity. This causes a crash. The IDE lies.
            //noinspection ConstantConditions
            if (pos != null)
            {
                // Get the state that the chisel would turn the block into if it clicked
                IBlockState newState = ItemMetalChisel.getChiselResultState(player, player.world, pos, traceResult.sideHit, (float) traceResult.hitVec.x - pos.getX(), (float) traceResult.hitVec.y - pos.getY(), (float) traceResult.hitVec.z - pos.getZ());
                if (newState != null)
                {
                    AxisAlignedBB box = getBox(player, pos, event.getPartialTicks()).grow(0.001);
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

                    drawBox(box);
                }
            }
        }
    }

    private static AxisAlignedBB getBox(EntityPlayer player, BlockPos pos, double partialTicks)
    {
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        return new AxisAlignedBB(pos).offset(-dx, -dy, -dz);
    }

    private static void drawBox(AxisAlignedBB box)
    {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth((float) 5.0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        RenderGlobal.drawSelectionBoundingBox(box, 1f, 0f, 0f, 0.8f);

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


}
