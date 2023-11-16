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
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.CrankshaftBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.ConnectedAxleBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.Node;

public class CrankshaftBlockEntityRenderer implements BlockEntityRenderer<CrankshaftBlockEntity>
{
    public static final ResourceLocation WHEEL_MODEL = Helpers.identifier("block/crankshaft_wheel");
    public static final ResourceLocation ROD_TEXTURE = Helpers.identifier("block/metal/block/steel");

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


        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());

        final float rotationAngle = crankshaft.getRotationAngle(partialTick);

        stack.pushPose();

        stack.translate(0.5f, 0, 0.5f);
        float rotation = switch (face)
        {
            case EAST -> 270f;
            case WEST -> 90f;
            case SOUTH -> 180f;
            default -> 0f;
        };
        stack.mulPose(Axis.YP.rotationDegrees(rotation));
        stack.translate(-0.5f, 0, -0.5f);

        if (part == CrankshaftBlock.Part.BASE)
        {
            stack.translate(0f, 0.5f, 0.5f);
            stack.mulPose(Axis.XN.rotation(-rotationAngle + Mth.PI));
            stack.translate(0f, -0.5f, -0.5f);

            final ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            final BakedModel baked = Minecraft.getInstance().getModelManager().getModel(WHEEL_MODEL);
            modelRenderer.tesselateWithAO(level, baked, crankshaft.getBlockState(), crankshaft.getBlockPos(), stack, buffer, true, level.getRandom(), packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());

            // Render an extension of the axle
            final BlockState adjacentAxleState = level.getBlockState(crankshaft.getBlockPos().relative(face.getCounterClockWise()));
            if (adjacentAxleState.getBlock() instanceof ConnectedAxleBlock axleBlock)
            {
                final ResourceLocation axleTexture = axleBlock.getAxleTextureLocation();
                final TextureAtlasSprite axleSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(axleTexture);

                RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, 0, 6 / 16f, 6 / 16f, 6 / 16f, 10 / 16f, 10 / 16f, false);
            }
        }
        else
        {
            final TextureAtlasSprite rodSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(ROD_TEXTURE);

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
            stack.translate(9 / 16f, 8 / 16f, totalOffset);

            // Draw the connecting (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -2 * px, -2 * px, -2 * px, 2 * px, 2 * px, 2 * px);

            // Draw the piston (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -px, -px, -17 * px, px, px, px);

            // Raise the connecting cuboid to the right angle and render
            stack.mulPose(Axis.XP.rotation(actualRaiseAngle));
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -px, -px, -px, px, px, 11 * px);
        }

        stack.popPose();
    }
}
