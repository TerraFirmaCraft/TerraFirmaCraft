/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.Nullable;

public interface IGhostBlockHandler
{
    /**
     * @return true to cancel the normal block highlighting
     */
    default boolean draw(Level level, Player player, BlockState lookState, BlockPos lookPos, Vec3 location, Direction lookDirection, PoseStack stack, MultiBufferSource buffer, ItemStack item)
    {
        final BlockState state = getStateToDraw(level, player, lookState, lookDirection, lookPos, location.x - lookPos.getX(), location.y - lookPos.getY(), location.z - lookPos.getZ(), item);
        if (state == null || !level.isClientSide) return false;

        return RenderHelpers.renderGhostBlock(level, state, lookPos, stack, buffer, shouldGrowSlightly(), Mth.floor(alpha() * 255));
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
        public VertexConsumer setColor(int r, int g, int b, int a)
        {
            return parent.setColor(r, g, b, (a * this.alpha) / 0xFF);
        }
    }
}
