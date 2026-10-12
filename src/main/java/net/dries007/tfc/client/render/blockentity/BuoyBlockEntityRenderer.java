/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.BuoyBlockEntity;
import net.dries007.tfc.util.Helpers;

public class BuoyBlockEntityRenderer implements BlockEntityRenderer<BuoyBlockEntity>
{
    private static final float BOB_SPEED = 0.06f;
    private static final float BOB_HEIGHT = 1 / 32f;
    private static final float ROCK_SPEED = 0.045f;
    private static final float ROCK_DEGREES = 6f;
    private static final float MAX_LEAN_DEGREES = 15f;
    private static final float SPIN_DEGREES_PER_TICK = 0.4f;
    private static final float PIVOT_Y = 3 / 16f;
    private static final float SINK_DEPTH = 5 / 16f; // Lowers the base of the model below the surface of the water in the block below
    private static final float ROPE_MIN = 7.5f / 16f;
    private static final float ROPE_MAX = 8.5f / 16f;
    private static final ResourceLocation ROPE_TEXTURE = Helpers.identifier("block/burlap");

    @Override
    public void render(BuoyBlockEntity buoy, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final Level level = buoy.getLevel();
        if (level == null)
        {
            return;
        }

        final BlockState state = buoy.getBlockState();
        final BlockPos pos = buoy.getBlockPos();
        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        final BakedModel model = dispatcher.getBlockModel(state);

        if (buoy.isTrapConnected())
        {
            renderRope(buoy, level, pos, stack, bufferSource, packedOverlay);
        }

        stack.pushPose();
        stack.translate(0.5f, PIVOT_Y - SINK_DEPTH, 0.5f);

        // seed by position
        final double time = buoy.getPhase(partialTick) + (HashCommon.mix(pos.asLong()) & 0xFFFF);
        final float windStrength = buoy.getWindStrength();
        final float rockDegrees = ROCK_DEGREES * (1 + windStrength * 1.5f);

        stack.translate(0, Math.sin(time * BOB_SPEED) * BOB_HEIGHT * (1 + windStrength), 0);

        // Lean downwind, by rotating around the horizontal axis perpendicular to the wind
        final float windX = buoy.getWindX(), windZ = buoy.getWindZ();
        final float windLength = Mth.sqrt(windX * windX + windZ * windZ);
        if (windLength > 1e-4f)
        {
            stack.mulPose(new Quaternionf().rotationAxis(windStrength * MAX_LEAN_DEGREES * Mth.DEG_TO_RAD, windZ / windLength, 0, -windX / windLength));
        }

        stack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(time * ROCK_SPEED) * rockDegrees));
        stack.mulPose(Axis.ZP.rotationDegrees((float) Math.cos(time * ROCK_SPEED * 0.7f) * rockDegrees));
        stack.mulPose(Axis.YP.rotationDegrees((float) ((time * SPIN_DEGREES_PER_TICK) % 360)));

        stack.translate(-0.5f, -PIVOT_Y, -0.5f);
        dispatcher.getModelRenderer().renderModel(stack.last(), bufferSource.getBuffer(RenderType.cutout()), state, model, 1f, 1f, 1f, packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());
        stack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(BuoyBlockEntity buoy)
    {
        final BlockPos pos = buoy.getBlockPos();
        return new AABB(pos.getX(), pos.getY() - buoy.getRopes() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    /**
     * Renders a vertical line from the base of the buoy to the top of the trap, one block at a time so each segment is lit by the block it passes through.
     */
    private void renderRope(BuoyBlockEntity buoy, Level level, BlockPos pos, PoseStack stack, MultiBufferSource bufferSource, int packedOverlay)
    {
        final TextureAtlasSprite sprite = RenderHelpers.blockTexture(ROPE_TEXTURE);
        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        final BlockPos.MutableBlockPos cursor = pos.mutable();
        for (int depth = 1; depth <= buoy.getRopes(); depth++)
        {
            final float top = Math.min(1 - depth, -SINK_DEPTH);
            final int light = LevelRenderer.getLightColor(level, cursor.move(0, -1, 0));
            final float bottom = -depth;
            final float width = 16f * (ROPE_MAX - ROPE_MIN), height = 16f * (top - bottom);

            RenderHelpers.renderTexturedQuads(stack, buffer, sprite, light, packedOverlay, RenderHelpers.getXVertices(ROPE_MIN, bottom, ROPE_MIN, ROPE_MAX, top, ROPE_MAX), width, height, 1, 0, 0, true);
            RenderHelpers.renderTexturedQuads(stack, buffer, sprite, light, packedOverlay, RenderHelpers.getZVertices(ROPE_MIN, bottom, ROPE_MIN, ROPE_MAX, top, ROPE_MAX), width, height, 0, 0, 1, true);
        }
    }
}
