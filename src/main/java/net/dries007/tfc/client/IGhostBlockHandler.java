/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Todo: this doesn't work yet for multipart baked models because they do crap with render types that is wrong.
 */
public interface IGhostBlockHandler
{
    /**
     * @return true to cancel the normal block highlighting
     */
    default boolean draw(Level level, Player player, BlockState lookState, BlockPos lookPos, Vec3 location, Direction lookDirection, PoseStack stack, MultiBufferSource buffer, ItemStack item)
    {
        final BlockState state = getStateToDraw(level, player, lookState, lookDirection, lookPos, location.x - lookPos.getX(), location.y - lookPos.getY(), location.z - lookPos.getZ(), item);
        if (state == null) return false;

        final Minecraft mc = Minecraft.getInstance();
        final BlockModelShaper shaper = mc.getBlockRenderer().getBlockModelShaper();
        final BakedModel model = shaper.getBlockModel(state);
        if (model == shaper.getModelManager().getMissingModel()) return false;

        final RenderType rt = Sheets.translucentCullBlockSheet();
        final VertexConsumer builder = new ForcedAlphaVertexConsumer(buffer.getBuffer(rt), Mth.floor(alpha() * 255));

        stack.pushPose();
        final Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        final Vec3 offset = Vec3.atLowerCornerOf(lookPos).subtract(camera);
        stack.translate(offset.x, offset.y, offset.z);
        if (shouldGrowSlightly())
        {
            stack.translate(-0.005F, -0.005F, -0.005F);
            stack.scale(1.01F, 1.01F, 1.01F);
        }
        final BlockRenderDispatcher br = Minecraft.getInstance().getBlockRenderer();

        for (RenderType type : model.getRenderTypes(state, level.random, ModelData.EMPTY))
        {
            br.renderBatched(state, lookPos, level, stack, builder, false, level.random, ModelData.EMPTY, rt);
        }

        RenderSystem.enableCull();
        ((MultiBufferSource.BufferSource) buffer).endBatch(rt);
        stack.popPose();
        return true;
    }

    /**
     * Controls if the model should be grown slightly to avoid clipping issues
     */
    default boolean shouldGrowSlightly()
    {
        return true;
    }

    /**
     * The transparency of the ghost block
     */
    default float alpha()
    {
        return 0.66F;
    }

    /**
     * @param player     The player.
     * @param lookState  The block 'highlighted' by the player
     * @param direction  The direction of the raytrace.
     * @param pos        The block position of the highilighted block
     * @param x          [0-1] The relative position of the hit in the x direction
     * @param y          [0-1] The relative position of the hit in the y direction
     * @param z          [0-1] The relative position of the hit in the z direction
     * @param item       The item held in the main hand.
     * @return The BlockState to be rendered as a ghost block, or null if nothing extra should be rendered.
     */
    @Nullable
    BlockState getStateToDraw(Level level, Player player, BlockState lookState, Direction direction, BlockPos pos, double x, double y, double z, ItemStack item);

    class ForcedAlphaVertexConsumer extends VertexConsumerWrapper
    {
        private final int alpha;

        public ForcedAlphaVertexConsumer(VertexConsumer wrapped, int alpha)
        {
            super(wrapped);
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a)
        {
            return parent.color(r, g, b, (a * this.alpha) / 0xFF);
        }
    }
}
