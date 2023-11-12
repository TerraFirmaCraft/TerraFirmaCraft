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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.CrankshaftBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.rotation.Node;

public class CrankshaftBlockEntityRenderer implements BlockEntityRenderer<CrankshaftBlockEntity>
{
    public CrankshaftBlockEntityRenderer(BlockEntityRendererProvider.Context ctx)
    {
        // todo: load a model for the wheel somehow?
    }

    @Override
    public void render(CrankshaftBlockEntity crankshaft, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final Level level = crankshaft.getLevel();
        final BlockPos pos = crankshaft.getBlockPos();
        final BlockState state = crankshaft.getBlockState();

        if (!(state.getBlock() instanceof CrankshaftBlock crankshaftBlock) || level == null)
        {
            return;
        }

        final Direction face = state.getValue(CrankshaftBlock.FACING);
        final CrankshaftBlock.Part part = state.getValue(CrankshaftBlock.PART);

        if (part == CrankshaftBlock.Part.SHAFT)
        {
            final BlockEntity mainPart = level.getBlockEntity(pos.relative(face, -1));
            if ((!(mainPart instanceof CrankshaftBlockEntity mainEntity)))
            {
                return;
            }
            crankshaft = mainEntity;
        }

        final BlockState adjacentAxleState = level.getBlockState(crankshaft.getBlockPos().relative(face.getCounterClockWise()));

        final AxleBlock axleBlock = adjacentAxleState.getBlock() instanceof AxleBlock b ? b : (AxleBlock) TFCBlocks.WOODS.get(Wood.ASH).get(Wood.BlockType.AXLE).get();

        final ResourceLocation axleTexture = axleBlock.getTextureLocation();
        final TextureAtlasSprite axleSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(axleTexture);
        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        final Node node = crankshaft.getRotationNode();

        // todo: make sure that getRotationAngle() is internally clamped to 2pi properly, on both sides
        float rotationAngle = crankshaft.getRotationAngle(partialTick);
        while (rotationAngle < 0)
        {
            rotationAngle += Mth.TWO_PI;
        }
        while (rotationAngle > Mth.TWO_PI)
        {
            rotationAngle -= Mth.TWO_PI;
        }


        stack.pushPose();

        stack.translate(0.5f, 0, 0.5f);
        stack.mulPose(Axis.YP.rotationDegrees(180f + 90f * face.get2DDataValue()));
        stack.translate(-0.5f, 0, -0.5f);

        if (part == CrankshaftBlock.Part.BASE)
        {
            stack.translate(0f, 0.5f, 0.5f);
            stack.mulPose(Axis.XN.rotation(rotationAngle));
            stack.translate(0f, -0.5f, -0.5f);

            // Render the wheel
            // todo: improve this to use a much better wheel
            RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, 5 / 16f, 4 / 16f, 4 / 16f, 7 / 16f, 12 / 16f, 12 / 16f);

            // Render an extension of the axle
            // todo: this needs to infer an axle more accurately, since it may be connected direct to a gearbox
            // todo: the quern hack needs to be fixed the same way

            RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, 0, 6 / 16f, 6 / 16f, 6 / 16f, 10 / 16f, 10 / 16f, false);

        }
        else
        {
            // Render three parts of the shaft
            // Two parts are static boxes that move back and forth
            // The third is the one that is rotated s.t. it connects to the wheel, and the other two
            final int quadrant = (int) (rotationAngle / Mth.HALF_PI);

            final float unitCircleAngle = switch (quadrant)
                {
                    case 3 -> rotationAngle;
                    case 2 -> rotationAngle - Mth.PI;
                    case 1 -> Mth.PI - rotationAngle;
                    default -> rotationAngle;
                };

            // All measurements are in pixels
            final float wheelRadius = 4 / 16f;
            final float armLength = 12 / 16f;
            final float px = (1 / 16f);

            final float offsetH = wheelRadius * Mth.sin(unitCircleAngle);
            final float offsetC = wheelRadius * Mth.cos(unitCircleAngle);
            final float offsetY = Mth.sqrt(armLength * armLength - offsetH * offsetH);

            final float raiseAngle = (float) Math.acos(offsetY / armLength);
            final float actualRaiseAngle = switch (quadrant)
                {
                    case 3, 2 -> raiseAngle;
                    default -> -raiseAngle;
                };

            // Offset from the center of the wheel, to the connecting point that moves horizontally
            final float totalOffset = (rotationAngle > Mth.HALF_PI && rotationAngle < Mth.PI + Mth.HALF_PI)
                ? offsetC + offsetY
                : -offsetC + offsetY;

            // Draw the two flat cuboids, based on `totalOffset`

            // Translate to the horizontal connecting point, so we can rotate from here
            stack.translate(9 / 16f, 8 / 16f, 1.5f - totalOffset);

            // Draw the connecting (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, -2 * px, -2 * px, -2 * px, 2 * px, 2 * px, 2 * px);

            // Draw the piston (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, -px, -px, -11 * px, px, px, px);

            // Raise the connecting cuboid to the right angle and render
            stack.mulPose(Axis.XP.rotation(actualRaiseAngle));
            RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, -px, -px, -px, px, px, 11 * px);
        }

        stack.popPose();
    }
}
