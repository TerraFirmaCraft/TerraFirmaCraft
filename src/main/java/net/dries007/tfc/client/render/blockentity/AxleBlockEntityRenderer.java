/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.function.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.AxleBlockEntity;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blocks.mechanical.AxleBlock;


public class AxleBlockEntityRenderer implements BlockEntityRenderer<AxleBlockEntity>
{
    public static void renderAxle(PoseStack stack, VertexConsumer buffer, ResourceLocation textureLocation, Direction.Axis axis, int packedLight, int packedOverlay, float rotationAngle)
    {
        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS);
        final TextureAtlasSprite sprite = atlas.apply(textureLocation);

        stack.pushPose();
        stack.translate(0.5f, 0.5f, 0.5f);

        switch (axis) {
            case X -> stack.mulPose(Axis.YP.rotationDegrees(90));
            case Y -> stack.mulPose(Axis.XP.rotationDegrees(90));
            case Z -> {}
        }

        stack.mulPose(Axis.ZP.rotation(rotationAngle));
        stack.translate(-0.5f, -0.5f, -0.5f);
        RenderHelpers.renderTexturedCuboid(stack, buffer, sprite, packedLight, packedOverlay, 6f / 16f, 6f / 16f, 0f, 10f / 16f, 10f / 16f, 1f);

    }

    @Override
    public void render(AxleBlockEntity axle, float partialTick, PoseStack stack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final BlockState state = axle.getBlockState();
        final Level level = axle.getLevel();

        if (!(state.getBlock() instanceof AxleBlock axleBlock) || level == null)
        {
            return;
        }

        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS);
        final TextureAtlasSprite sprite = atlas.apply(axleBlock.getTextureLocation());
        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

        renderAxle(stack, buffer, axleBlock.getTextureLocation(), state.getValue(AxleBlock.AXIS), packedLight, packedOverlay, -axle.getRotationAngle(partialTick));

        final BlockPos below = axle.getBlockPos().below();
        final boolean connectedToQuern = level.getBlockEntity(below) instanceof QuernBlockEntity quern && quern.hasHandstone() && state.getValue(AxleBlock.AXIS).isVertical();

        if (connectedToQuern)
        {
            stack.translate(0, 0, 1); // This is correct, because we rotated into the Z orientation first, so +Z is down
            RenderHelpers.renderTexturedCuboid(stack, buffer, sprite, packedLight, packedOverlay, 6f / 16f, 6f / 16f, 0f, 10f / 16f, 10f / 16f, 0.5f);
        }

        stack.popPose();
    }
}
