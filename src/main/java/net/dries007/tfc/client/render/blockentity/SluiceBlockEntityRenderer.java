/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.SluiceBlockEntity;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;

public class SluiceBlockEntityRenderer implements BlockEntityRenderer<SluiceBlockEntity>
{
    private static void drawItem(ItemStack stack, float x, float y, float z, float rotation, ItemRenderer renderer, PoseStack poseStack, int combinedLight, int combinedOverlay, MultiBufferSource buffer)
    {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(0.3F, 0.3F, 0.3F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        renderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
        poseStack.popPose();
    }

    private static void vertex(QuadRenderInfo info, float x, float y, float z, int color, float u, float v)
    {
        info.builder.vertex(info.matrix4f, x, y, z).color(color).uv(u, v).overlayCoords(info.combinedOverlay).uv2(info.combinedLight).normal(0, 0, 1).endVertex();
    }

    @Override
    @SuppressWarnings("deprecation")
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
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        }
        else if (facing == Direction.EAST)
        {
            poseStack.translate(0F, 0F, 1F);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        }
        poseStack.mulPose(Vector3f.YP.rotationDegrees(state.getValue(SluiceBlock.FACING).get2DDataValue() * 90F));

        final float rotation = RenderHelpers.itemTimeRotation();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        sluice.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
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
                    drawItem(stack, x, y, z, rotation, itemRenderer, poseStack, combinedLight, combinedOverlay, buffer);
                }
            }
        });

        final Fluid fluid = sluice.getFlow();
        if (fluid == null)
        {
            poseStack.popPose();
            return;
        }

        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation texture = attributes.getStillTexture();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
        final int color = RenderHelpers.getFluidColor(fluid);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
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
