/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.LobsterTrapBlockEntity;
import net.dries007.tfc.common.blocks.LobsterTrapBlock;

public class LobsterTrapBlockEntityRenderer implements BlockEntityRenderer<LobsterTrapBlockEntity>
{
    // Slot positions on the floor of the trap, in pixels, for a trap with its opening facing north
    private static final float[][] SLOT_POSITIONS = {
        {4, 8}, {8, 8}, {12, 8},
        {4, 12}, {8, 12}, {12, 12}
    };

    @Override
    public void render(LobsterTrapBlockEntity trap, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final BlockState state = trap.getBlockState();
        if (!(state.getBlock() instanceof LobsterTrapBlock))
        {
            return;
        }

        stack.pushPose();
        stack.translate(0.5f, 0, 0.5f);
        stack.mulPose(Axis.YP.rotationDegrees(180f - state.getValue(LobsterTrapBlock.FACING).toYRot()));
        stack.translate(-0.5f, 0, -0.5f);

        for (int slot = 0; slot < LobsterTrapBlockEntity.CATCH_SLOTS; slot++)
        {
            final ItemStack item = trap.getInventory().getStackInSlot(slot);
            if (!item.isEmpty())
            {
                stack.pushPose();
                stack.translate(SLOT_POSITIONS[slot][0] / 16f, 1.1f / 16f, SLOT_POSITIONS[slot][1] / 16f);
                stack.mulPose(Axis.XP.rotationDegrees(90f));
                stack.mulPose(Axis.ZP.rotationDegrees(slot * 47f)); // Vary the angle so the catch looks less uniform
                stack.scale(0.35f, 0.35f, 0.35f);
                Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, stack, bufferSource, trap.getLevel(), 0);
                stack.popPose();
            }
        }

        stack.popPose();
    }
}
