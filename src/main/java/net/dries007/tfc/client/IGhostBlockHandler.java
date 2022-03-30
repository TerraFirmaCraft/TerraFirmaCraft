/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public interface IGhostBlockHandler
{
    /**
     * @return true to cancel the normal block highlighting
     */
    default boolean draw(Level level, Player player, BlockState lookState, BlockPos lookPos, Vec3 location, Direction lookDirection, PoseStack stack, MultiBufferSource buffer, ItemStack item)
    {
        BlockState state = getStateToDraw(level, player, lookState, lookDirection, lookPos, location.x - lookPos.getX(), location.y - lookPos.getY(), location.z - lookPos.getZ(), item);
        if (state == null) return false;

        Minecraft mc = Minecraft.getInstance();
        BlockModelShaper shaper = mc.getBlockRenderer().getBlockModelShaper();
        BakedModel model = shaper.getBlockModel(state);
        if (model == shaper.getModelManager().getMissingModel()) return false;

        ForgeHooksClient.setRenderType(RenderType.translucent());
        VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());

        stack.pushPose();
        final Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        stack.translate(-camera.x, -camera.y, -camera.z);
        stack.translate(lookPos.getX(), lookPos.getY(), lookPos.getZ());
        if (shouldGrowSlightly())
        {
            stack.translate(-0.005F, -0.005F, -0.005F);
            stack.scale(1.01F, 1.01F, 1.01F);
        }
        final PoseStack.Pose pose = stack.last();

        Arrays.stream(ClientHelpers.DIRECTIONS_AND_NULL)
            .flatMap(dir -> model.getQuads(state, dir, level.random, EmptyModelData.INSTANCE).stream())
            .forEach(quad -> consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, alpha(), LevelRenderer.getLightColor(level, state, lookPos), OverlayTexture.NO_OVERLAY));

        ((MultiBufferSource.BufferSource) buffer).endBatch(RenderType.translucent());
        ForgeHooksClient.setRenderType(null);

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
}
