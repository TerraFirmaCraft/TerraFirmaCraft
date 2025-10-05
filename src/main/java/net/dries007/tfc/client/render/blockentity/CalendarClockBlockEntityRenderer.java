package net.dries007.tfc.client.render.blockentity;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.CalendarClockModel;
import net.dries007.tfc.common.blockentities.CalendarClockBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class CalendarClockBlockEntityRenderer implements BlockEntityRenderer<CalendarClockBlockEntity>
{
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/entity/calendar_clock.png");
    public static final ResourceLocation TEXTURE_MONTH = Helpers.identifier("textures/entity/calendar_clock_month.png");

    private final CalendarClockModel model;

    public CalendarClockBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.model = new CalendarClockModel(context.bakeLayer(RenderHelpers.layerId("calendar_clock")));
    }

    @Override
    public void render(CalendarClockBlockEntity clock, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (clock.getLevel() == null)
        {
            return;
        }

        stack.pushPose();

        stack.translate(0.5f, -0f, 0.5f);
        stack.translate(0, 1.0625f + 0.4375f, 0);
        stack.scale(-1, -1, 1);

        model.setupAnim(clock, partialTick);

        if (clock.getBlockState().getValue(TFCBlockStateProperties.CLOCK_MONTH_MODE))
        {
            model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_MONTH)), packedLight, packedOverlay, -1);
        }
        else
        {
            model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, packedOverlay, -1);
        }

        stack.popPose();
    }
}
