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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.CrankshaftBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rotation.ConnectedAxleBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.rotation.FluidPumpBlock;
import net.dries007.tfc.util.Helpers;


public class CrankshaftBlockEntityRenderer implements BlockEntityRenderer<CrankshaftBlockEntity>
{
    public static final ResourceLocation WHEEL_MODEL = Helpers.identifier("block/crankshaft_wheel");
    public static final ResourceLocation ROD_TEXTURE = Helpers.identifier("block/metal/block/steel");
    public static final ResourceLocation PUMP_TEXTURE = Helpers.identifier("block/metal/smooth/brass");

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

        // Normally, rotation is never set on the shaft part, as that node never connects itself to the network
        // However when we render via Patchouli's multiblock system, we can't query the world for adjacent blocks,
        // So we have to rely on the rotation set via hacks directly onto the shaft.
        if (part == CrankshaftBlock.Part.SHAFT && crankshaft.getRotationNode().rotation() == null)
        {
            final BlockEntity mainPart = level.getBlockEntity(pos.relative(face, -1));
            if ((!(mainPart instanceof CrankshaftBlockEntity mainEntity)))
            {
                return;
            }
            crankshaft = mainEntity;
        }


        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        final float rotationAngle = CrankshaftBlockEntity.calculateRealRotationAngle(crankshaft, face, partialTick);
        final RandomSource random = RandomSource.create();

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

            modelRenderer.tesselateWithAO(level, baked, crankshaft.getBlockState(), crankshaft.getBlockPos(), stack, buffer, true, random, packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());

            // Render an extension of the axle
            final BlockState adjacentAxleState = level.getBlockState(crankshaft.getBlockPos().relative(face.getCounterClockWise()));
            if (adjacentAxleState.getBlock() instanceof ConnectedAxleBlock axleBlock && crankshaft.getRotationNode().isConnectedToNetwork())
            {
                final ResourceLocation axleTexture = axleBlock.getAxleTextureLocation();
                final TextureAtlasSprite axleSprite = RenderHelpers.blockTexture(axleTexture);

                RenderHelpers.renderTexturedCuboid(stack, buffer, axleSprite, packedLight, packedOverlay, 0, 6 / 16f, 6 / 16f, 6 / 16f, 10 / 16f, 10 / 16f, false);
            }
        }
        else
        {
            stack.translate(-0.5f, -0.5f, -0.5f);

            final TextureAtlasSprite rodSprite = RenderHelpers.blockTexture(ROD_TEXTURE);

            final var movement = CrankshaftBlockEntity.calculateShaftMovement(rotationAngle);

            final float pistonLength = CrankshaftBlockEntity.PISTON_LENGTH + 1 / 32f; // + 1 / 32f for not being _exactly_ on the border, to stop z-fighting
            final float armLength = CrankshaftBlockEntity.ARM_LENGTH;
            final float armRadius = 1 / 16f; // Half of the thickness of the arm
            final float boxRadius = 2 / 16f; // The connecting box

            // Draw the two flat cuboids, based on `totalOffset`

            // Translate to the (center of the) horizontal connecting point, so we can rotate from here
            stack.translate(8 / 16f + armRadius, 8 / 16f, movement.lengthEH());

            // Draw the connecting (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -boxRadius, -boxRadius, -boxRadius, boxRadius, boxRadius, boxRadius);

            // Draw the piston (flat) cuboid
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -armRadius, -armRadius, -pistonLength, armRadius, armRadius, armRadius);

            // Render a small attachment if the shaft is connected to a pump
            final BlockPos pumpPos = pos.relative(face);
            final BlockState pumpState = level.getBlockState(pumpPos);
            if (pumpState.getBlock() == TFCBlocks.STEEL_PUMP.get() && face == pumpState.getValue(FluidPumpBlock.FACING))
            {
                RenderHelpers.renderTexturedCuboid(stack, buffer, RenderHelpers.blockTexture(PUMP_TEXTURE), packedLight, packedOverlay, -2 / 16f, -2 / 16f, -pistonLength - 3 / 32f, 2 / 16f, 2 / 16f, -pistonLength + 5 / 32f);
            }

            // Raise the connecting cuboid to the right angle and render
            // +1 / -1's all around due to the radius of the arm around the line segment
            stack.mulPose(Axis.XP.rotation(movement.raiseAngle()));
            RenderHelpers.renderTexturedCuboid(stack, buffer, rodSprite, packedLight, packedOverlay, -armRadius, -armRadius, -armRadius, armRadius, armRadius, armLength + armRadius);
        }

        stack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(CrankshaftBlockEntity entity)
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(CrankshaftBlockEntity blockEntity)
    {
        return AABB.INFINITE;
    }
}
