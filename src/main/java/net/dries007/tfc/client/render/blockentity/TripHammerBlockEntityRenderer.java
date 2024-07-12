/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.common.blocks.TripHammerBlock;
import net.dries007.tfc.common.items.HammerItem;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.Rotation;

public class TripHammerBlockEntityRenderer implements BlockEntityRenderer<TripHammerBlockEntity>
{
    private static final ResourceLocation ROD_TEXTURE = Helpers.identifier("block/wood/planks/oak");

    @Override
    public void render(TripHammerBlockEntity hammer, float partialTick, PoseStack stack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final BlockState state = hammer.getBlockState();
        final Level level = hammer.getLevel();
        final Rotation rotation = hammer.getRotation();

        if (level == null || !(state.getBlock() instanceof TripHammerBlock))
            return;

        final ItemStack item = hammer.getInventory().getStackInSlot(0);
        final ResourceLocation hammerTexture = item.getItem() instanceof HammerItem hammerItem ? hammerItem.getMetalTexture() : null;
        if (hammerTexture == null)
            return;

        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());
        final float px = 1f / 16f;

        stack.pushPose();

        stack.translate(0.5f, 0.5f, 0.5f);
        stack.mulPose(Axis.YP.rotationDegrees(180f - 90f * state.getValue(TripHammerBlock.FACING).get2DDataValue()));
        stack.translate(-0.5f, -0.5f, -0.5f);

        final float angle = rotation == null ? 0 : 360f - hammer.getRealRotationDegrees(rotation, partialTick);
        final float pivotStart = 130f;
        final float pivotMiddle = 180f;
        final float pivotEnd = 183f;

        float pivotAngle = 0f;

        if (angle > pivotStart && angle < pivotMiddle)
        {
            pivotAngle = Mth.map(angle, pivotStart, pivotMiddle, 0f, 45f);
        }
        else if (angle > pivotMiddle && angle < pivotEnd)
        {
            pivotAngle = 45f - Mth.map(angle, pivotMiddle, pivotEnd, 0f, 45f);
        }
        final float pivotX = 8f * px;
        final float pivotY = 14f * px;
        final float pivotZ = 3f * px;

        if (pivotAngle != 0f && rotation.positiveDirection() == state.getValue(TripHammerBlock.FACING).getClockWise())
        {
            stack.translate(pivotX, pivotY, pivotZ);

            stack.mulPose(Axis.XN.rotationDegrees(-pivotAngle));

            stack.translate(-pivotX, -pivotY, -pivotZ);
        }

        RenderHelpers.renderTexturedCuboid(stack, buffer, RenderHelpers.blockTexture(ROD_TEXTURE), packedLight, packedOverlay, 6.5f * px, 12.5f * px, -6f * px, 9.5f * px, 15.5f * px, 10f * px, false);
        RenderHelpers.renderTexturedCuboid(stack, buffer, RenderHelpers.blockTexture(hammerTexture), packedLight, packedOverlay, 5.5f * px, 11f * px, -11f * px, 10.5f * px, 17f * px, -6f * px, false);

        stack.popPose();
    }
}
