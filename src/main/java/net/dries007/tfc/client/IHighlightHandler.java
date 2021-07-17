package net.dries007.tfc.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.mixin.client.renderer.WorldRendererAccessor;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public interface IHighlightHandler
{

    /**
     * Draws the outlining of a VoxelShape
     *
     * @param stack     Matrix Stack to draw on
     * @param shape     VoxelShape to draw
     * @param buffers   IRenderTypeBuffer to use
     * @param pos       Block position
     * @param red       [0-1] red value
     * @param green     [0-1] green value
     * @param blue      [0-1] blue value
     * @param alpha     [0-1] alpha value
     */
    static void drawBox(MatrixStack stack, VoxelShape shape, IRenderTypeBuffer buffers, BlockPos pos, Vector3d renderPos, float red, float green, float blue, float alpha)
    {
        WorldRendererAccessor.invoke$renderShape(stack, buffers.getBuffer(RenderType.lines()), shape, pos.getX() - renderPos.x, pos.getY() - renderPos.y, pos.getZ() - renderPos.z, red, green, blue, alpha);
    }


    /**
     * Handles drawing custom bounding boxes depending on where player is looking at
     *
     * @param world        the client's world obj
     * @param pos          the blockpos player is looking at
     * @param player       the player that is looking at this block
     * @param rayTrace     the RayTraceResult, got from DrawBlockHighlightEvent
     * @param matrixStack  current Matrix Stack
     * @param buffers      render buffer
     * @param rendererPosition where the renderer is right now (essentially partial ticks)
     * @return true if you wish to cancel drawing the block's bounding box outline
     */
    boolean drawHighlight(World world, BlockPos pos, PlayerEntity player, BlockRayTraceResult rayTrace, MatrixStack matrixStack, IRenderTypeBuffer buffers, Vector3d rendererPosition);

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
    final class EventHandler
    {
        /**
         * Handles custom bounding boxes drawing
         * eg: Chisel, Quern handle
         */
        @SubscribeEvent
        public static void drawHighlightEvent(DrawHighlightEvent.HighlightBlock event)
        {
            final ActiveRenderInfo info = event.getInfo();
            final MatrixStack mStack = event.getMatrix();
            final Entity entity = info.getEntity();
            final World world = entity.level;
            final BlockRayTraceResult traceResult = event.getTarget();
            final BlockPos lookingAt = new BlockPos(traceResult.getLocation());

            //noinspection ConstantConditions
            if (lookingAt != null && entity instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) entity;
                Block blockAt = world.getBlockState(lookingAt).getBlock();
                //todo: chisel
                if (blockAt instanceof IHighlightHandler) //todo: java 16
                {
                    // Pass on to custom implementations
                    IHighlightHandler handler = (IHighlightHandler) blockAt;
                    if (handler.drawHighlight(world, lookingAt, player, traceResult, mStack, event.getBuffers(), info.getPosition()))
                    {
                        // Cancel drawing this block's bounding box
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
