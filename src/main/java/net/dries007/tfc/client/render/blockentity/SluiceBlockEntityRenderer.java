/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.SluiceBlockEntity;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.util.Helpers;

public class SluiceBlockEntityRenderer implements BlockEntityRenderer<SluiceBlockEntity>
{
    private static void drawItem(ItemStack stack, float x, float y, float z, float rotation, ItemRenderer renderer, PoseStack poseStack, int combinedLight, int combinedOverlay, MultiBufferSource buffer, @Nullable Level level)
    {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(0.3F, 0.3F, 0.3F);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        renderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, level, 0);
        poseStack.popPose();
    }

    private static void vertex(QuadRenderInfo info, float x, float y, float z, int color, float u, float v)
    {
        info.builder.vertex(info.matrix4f, x, y, z).color(color).uv(u, v).overlayCoords(info.combinedOverlay).uv2(info.combinedLight).normal(0, 0, 1).endVertex();
    }

    @Override
    public void render(SluiceBlockEntity sluice, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (sluice.getLevel() == null) return;
        final BlockState state = sluice.getBlockState();
        if (!state.getValue(SluiceBlock.UPPER)) return;

        poseStack.pushPose();
        final Direction facing = state.getValue(SluiceBlock.FACING);
        if (facing == Direction.NORTH)
        {
            poseStack.translate(1F, 0F, 1F);
        }
        else if (facing == Direction.WEST)
        {
            poseStack.translate(1F, 0F, 0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        }
        else if (facing == Direction.EAST)
        {
            poseStack.translate(0F, 0F, 1F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(SluiceBlock.FACING).get2DDataValue() * 90F));

        final float rotation = RenderHelpers.itemTimeRotation();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        sluice.getCapability(Capabilities.ITEM).ifPresent(inv -> {
            for (int i = 0; i < inv.getSlots(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    final int step = Mth.floor((float) i / 4F);
                    final int across = i % 4;
                    final float x = 0.125F + (0.25F * across);
                    final float y = 0.96875F - 0.0125F - (0.125F * step);
                    final float z = 0.15625F - 0.0125F + (0.25F * step);
                    drawItem(stack, x, y, z, rotation, itemRenderer, poseStack, combinedLight, combinedOverlay, buffer, sluice.getLevel());
                }
            }
        });

        final Fluid fluid = sluice.getFlow();
        if (fluid == null)
        {
            poseStack.popPose();
            return;
        }

        FluidType attributes = fluid.getFluidType();
        IClientFluidTypeExtensions extension = IClientFluidTypeExtensions.of(attributes);
        ResourceLocation texture = extension.getStillTexture();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(texture);
        final int color = Helpers.isFluid(fluid, TFCTags.Fluids.ANY_INFINITE_WATER) ? TFCColors.getWaterColor(sluice.getBlockPos()) : RenderHelpers.getFluidColor(fluid);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentCull(RenderHelpers.BLOCKS_ATLAS));
        Matrix4f matrix4f = poseStack.last().pose();

        final QuadRenderInfo info = new QuadRenderInfo(builder, matrix4f, combinedOverlay, combinedLight);
        // Top
        vertex(info, 0.05F, 1.033F, 0F, color, sprite.getU0(), sprite.getV0());
        vertex(info, 0.05F, -0.15F, 2.45F, color, sprite.getU0(), sprite.getV1());
        vertex(info, 0.95F, -0.15F, 2.45F, color, sprite.getU1(), sprite.getV1());
        vertex(info, 0.95F, 1.033F, 0F, color, sprite.getU1(), sprite.getV0());

        // Bottom
        vertex(info, 0.05F, 0.833F, 0F, color, sprite.getU0(), sprite.getV0());
        vertex(info, 0.05F, -0.3F, 2.45F, color, sprite.getU0(), sprite.getV1());
        vertex(info, 0.95F, -0.3F, 2.45F, color, sprite.getU1(), sprite.getV1());
        vertex(info, 0.95F, 0.833F, 0F, color, sprite.getU1(), sprite.getV0());

        // Left
        vertex(info, 0.05F, -0.15F, 2.45F, color, sprite.getU0(), sprite.getV0());
        vertex(info, 0.05F, 1.033F, 0F, color, sprite.getU0(), sprite.getV1());
        vertex(info, 0.05F, 0.833F, 0F, color, sprite.getU1(), sprite.getV1());
        vertex(info, 0.05F, -0.3F, 2.45F, color, sprite.getU1(), sprite.getV0());

        // Right
        vertex(info, 0.95F, 1.033F, 0F, color, sprite.getU0(), sprite.getV0());
        vertex(info, 0.95F, -0.15F, 2.45F, color, sprite.getU0(), sprite.getV1());
        vertex(info, 0.95F, -0.3F, 2.45F, color, sprite.getU1(), sprite.getV1());
        vertex(info, 0.95F, 0.833F, 0F, color, sprite.getU1(), sprite.getV0());

        poseStack.popPose();
    }

    private record QuadRenderInfo(VertexConsumer builder, Matrix4f matrix4f, int combinedOverlay, int combinedLight) {}
}
