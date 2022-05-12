/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.mixin.client.accessor.LevelRendererAccessor;

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
    static void drawBox(PoseStack stack, VoxelShape shape, MultiBufferSource buffers, BlockPos pos, Vec3 renderPos, float red, float green, float blue, float alpha)
    {
        LevelRendererAccessor.invoke$renderShape(stack, buffers.getBuffer(RenderType.lines()), shape, pos.getX() - renderPos.x, pos.getY() - renderPos.y, pos.getZ() - renderPos.z, red, green, blue, alpha);
    }

    /**
     * Handles drawing custom bounding boxes depending on where player is looking at
     *
     * @param world        the client's world obj
     * @param pos          the blockpos player is looking at
     * @param player       the player that is looking at this block
     * @param rayTrace     the HitResult, got from DrawBlockHighlightEvent
     * @param stack        current Pose Stack
     * @param buffers      render buffer
     * @param rendererPosition where the renderer is right now (essentially partial ticks)
     * @return true if you wish to cancel drawing the block's bounding box outline
     */
    boolean drawHighlight(Level world, BlockPos pos, Player player, BlockHitResult rayTrace, PoseStack stack, MultiBufferSource buffers, Vec3 rendererPosition);
}
