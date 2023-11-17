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
import net.dries007.tfc.common.blocks.rotation.ConnectedAxleBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.util.Helpers;


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

        if (!(state.getBlock() instanceof CrankshaftBlock) || level == null)
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

        final float sourceRotationAngle = crankshaft.getRotationAngle(partialTick);
        final float rotationAngle = face == Direction.NORTH || face == Direction.EAST ? Mth.TWO_PI - sourceRotationAngle : sourceRotationAngle;

        stack.pushPose();
        stack.translate(0.5f, 0.5f, 0.5f);
        stack.mulPose(Axis.YP.rotationDegrees(180f - 90f * face.get2DDataValue()));

        if (part == CrankshaftBlock.Part.BASE)
        {
            stack.mulPose(Axis.XP.rotation(rotationAngle + Mth.PI));
            stack.translate(-0.5f, -0.5f, -0.5f);

            // Render the attached wheel, rotating with the axle
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
            stack.translate(-0.5f, -0.5f, -0.5f);

            final TextureAtlasSprite rodSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(ROD_TEXTURE);

            // Render three parts of the shaft
            // Two parts are static boxes that move back and forth
            // The third is the one that is rotated s.t. it connects to the wheel, and the other two
            final int quadrant = Mth.clamp((int) (rotationAngle / Mth.HALF_PI), 0, 3);

            final float unitCircleAngle = quadrant == 0 || quadrant == 3
                ? rotationAngle
                : (quadrant == 1
                    ? Mth.PI - rotationAngle
                    : rotationAngle - Mth.PI);

            // All measurements are in pixels
            final float wheelRadius = 2.5f / 16f; // todo: wider wheel? And then measure center <-> center point to connecting arm, and change here.
            final float armLength = 12 / 16f;
            final float armRadius = 1 / 16f; // Half of the thickness of the arm
            final float px = (1 / 16f);

            // Where:
            //   O := The center of the circle with radius R
            //        The radius is the distance to the _center_ of the connecting point - not the whole radius of the wheel
            //   H := The center of the connecting box
            //   A := The point where the line OH intersects the circle
            //   B := A point on the radius of the circle s.t. the angle HOB is ~45° - call this angle theta
            //   C := A point on the line OH s.t. angles OCB and HCB are 90°
            //
            // Then, relative to the shaft block's origin, where the direction conventions are:
            //   +x := Into the page (The axis of the connected axle)
            //   +y := Up
            //   +z := Left (the axis of the shaft)
            //
            // We have the following positions:
            //
            // O = (0.5, 0.5, 1.5)
            //   Note that the X value describes the point touching the wheel, but the midpoint of the shaft is actually 0.5 + armRadius
            //
            // |OB| = |OA| = R
            // |BH| = armLength, but the length between connecting points (so total length rendered += 2 x armRadius)
            // |CB| = R sin theta
            // |OC| = R cos theta
            // L^2 = |CB|^2 + |CH|^2

            final float lengthCB = wheelRadius * Mth.sin(unitCircleAngle);
            final float lengthOC = wheelRadius * Mth.cos(unitCircleAngle);
            final float lengthCH = Mth.sqrt(armLength * armLength - lengthCB * lengthCB);

            final float angleHCB = (float) Math.acos(lengthCH / armLength);

            final float raiseAngle = quadrant == 2 || quadrant == 3
                ? angleHCB
                : -angleHCB;

            // Draw the two flat cuboids, based on `totalOffset`

            // Translate to the (center of the) horizontal connecting point, so we can rotate from here
            stack.translate(8 / 16f + armRadius, 8 / 16f, 1.5f - lengthCH + (quadrant == 1 || quadrant == 2 ? lengthOC : -lengthOC));

            // Draw the connecting (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -2 * px, -2 * px, -2 * px, 2 * px, 2 * px, 2 * px);

            // Draw the piston (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -px, -px, -17 * px, px, px, px);

            // Raise the connecting cuboid to the right angle and render
            // +1 / -1's all around due to the radius of the arm around the line segment
            stack.mulPose(Axis.XP.rotation(raiseAngle));
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -px, -px, -px, px, px, armLength + px);
        }

        stack.popPose();
    }
}
