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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.WindmillBladeModel;
import net.dries007.tfc.common.blockentities.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.mechanical.WindmillBlock;
import net.dries007.tfc.util.Helpers;

public class WindmillBlockEntityRenderer implements BlockEntityRenderer<WindmillBlockEntity>
{
    public static final ResourceLocation BLADE_TEXTURE = Helpers.identifier("textures/entity/misc/windmill_blade.png");

    private final WindmillBladeModel blade;

    public WindmillBlockEntityRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.blade = new WindmillBladeModel(ctx.bakeLayer(RenderHelpers.modelIdentifier("windmill_blade")));
    }

    @Override
    public void render(WindmillBlockEntity windmill, float partialTick, PoseStack stack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final Level level = windmill.getLevel();
        final BlockState state = windmill.getBlockState();

        if (!(state.getBlock() instanceof WindmillBlock windmillBlock) || level == null)
        {
            return;
        }

        final Direction.Axis axis = state.getValue(WindmillBlock.AXIS);
        final ResourceLocation axleTexture = windmillBlock.getAxle().getTextureLocation();
        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());
        final int bladeCount = state.getValue(WindmillBlock.COUNT);

        AxleBlockEntityRenderer.renderAxle(stack, buffer, axleTexture, axis, packedLight, packedOverlay, -windmill.getRotationAngle(partialTick));

        stack.popPose(); // renderAxle() pushes a pose, so we pop that one
        stack.pushPose();

        final boolean axisX = state.getValue(WindmillBlock.AXIS) == Direction.Axis.X;

        if (!axisX)
        {
            stack.mulPose(Axis.YP.rotationDegrees(-90f));
        }

        stack.translate(0.5f, -1, axisX ? 0.5f : -0.5f);

        final float offsetAngle = Mth.TWO_PI / bladeCount;
        for (int i = 0; i < bladeCount; i++)
        {
            stack.pushPose();
            blade.setupAnim(windmill, partialTick, offsetAngle * i);
            blade.renderToBuffer(stack, buffers.getBuffer(RenderType.entityCutout(BLADE_TEXTURE)), packedLight, packedOverlay, 1f, 1f, 1f, 1f);
            stack.popPose();
        }

        stack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(WindmillBlockEntity windmill)
    {
        return true;
    }

    @Override
    public boolean shouldRender(WindmillBlockEntity windmill, Vec3 cameraPos)
    {
        return true;
    }
}
