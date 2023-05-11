package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.WindmillBladeModel;
import net.dries007.tfc.common.blockentities.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.mechanical.WindmillBlock;
import net.dries007.tfc.util.Helpers;

public class WindmillBlockEntityRenderer implements BlockEntityRenderer<WindmillBlockEntity>
{
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/entity/misc/windmill_blade.png");

    private final WindmillBladeModel blade;

    public WindmillBlockEntityRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.blade = new WindmillBladeModel(ctx.bakeLayer(RenderHelpers.modelIdentifier("windmill_blade")));
    }

    @Override
    public void render(WindmillBlockEntity mill, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        if (mill.getLevel() == null)
        {
            return;
        }
        final int count = mill.getBlockState().getValue(WindmillBlock.STAGE);
        if (count == 0)
        {
            return;
        }

        poseStack.pushPose();
        if (mill.getBlockState().getValue(WindmillBlock.AXIS) == Direction.Axis.X)
        {
            poseStack.mulPose(RenderHelpers.rotateDegreesY(90f));
        }

        poseStack.translate(-3 / 16f, -1, 0.5f);

        final float rot = 360f / count;
        for (int i = 0; i < count; i++)
        {
            poseStack.pushPose();
            blade.setupAnim(mill, partialTick, rot * i);
            blade.renderToBuffer(poseStack, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), packedLight, packedOverlay, 1f, 1f, 1f, 1f);
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
