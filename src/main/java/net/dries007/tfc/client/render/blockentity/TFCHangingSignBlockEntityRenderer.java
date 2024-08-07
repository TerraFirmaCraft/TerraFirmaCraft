/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.mixin.client.accessor.SignRendererAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class TFCHangingSignBlockEntityRenderer extends HangingSignRenderer
{
    public static final Map<Block, Provider<Function<BlockEntityRendererProvider.Context, HangingSignModel>>> MODELS = RenderHelpers.mapOf(map -> {
        TFCBlocks.CEILING_HANGING_SIGNS.forEach((wood, m) -> m.forEach((metal, block) -> {
            final var model = new Provider<Function<BlockEntityRendererProvider.Context, HangingSignModel>>(
                new Material(
                    Sheets.SIGN_SHEET,
                    Helpers.identifier("entity/signs/hanging/" + metal.getSerializedName() + "/" + wood.getSerializedName())
                ),
                Helpers.resourceLocation(wood.getSerializedName() + ".png").withPrefix("textures/gui/hanging_signs/" + metal.getSerializedName() + "/"),
                context -> new HangingSignModel(context.bakeLayer(RenderHelpers.layerId("hanging_sign/" + wood.getSerializedName())))
            );

            map.accept(block, model);
            map.accept(TFCBlocks.WALL_HANGING_SIGNS.get(wood).get(metal), model);
        }));
    });

    private final Map<Block, Provider<HangingSignModel>> hangingSignModels;

    public TFCHangingSignBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        hangingSignModels = Helpers.mapValue(MODELS, v -> new Provider<>(v.modelMaterial, v.textureLocation, v.model.apply(context)));
    }

    @Override
    public void render(SignBlockEntity sign, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        final BlockState state = sign.getBlockState();
        final SignBlock signBlock = (SignBlock) state.getBlock();
        final @Nullable Provider<HangingSignModel> model = hangingSignModels.get(state.getBlock());
        if (model == null)
        {
            return;
        }

        model.model.evaluateVisibleParts(state);
        renderSignWithText(sign, poseStack, buffer, light, overlay, state, signBlock, model.modelMaterial(), model.model);
    }

    // behavior copied from SignRenderer#renderSignWithText
    void renderSignWithText(SignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, BlockState blockstate, SignBlock signblock, Material modelMaterial, Model model)
    {
        poseStack.pushPose();
        ((SignRendererAccessor) this).invoke$translateSign(poseStack, -signblock.getYRotationDegrees(blockstate), blockstate);
        this.renderSign(poseStack, buffer, light, overlay, modelMaterial, model);
        ((SignRendererAccessor) this).invoke$renderSignText(sign.getBlockPos(), sign.getFrontText(), poseStack, buffer, light, sign.getTextLineHeight(), sign.getMaxTextLineWidth(), true);
        ((SignRendererAccessor) this).invoke$renderSignText(sign.getBlockPos(), sign.getBackText(), poseStack, buffer, light, sign.getTextLineHeight(), sign.getMaxTextLineWidth(), false);
        poseStack.popPose();
    }

    // behavior copied from SignRenderer#renderSign
    void renderSign(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, Material modelMaterial, Model model)
    {
        poseStack.pushPose();
        float f = this.getSignModelRenderScale();
        poseStack.scale(f, -f, -f);

        VertexConsumer vertexconsumer = modelMaterial.buffer(buffer, model::renderType);
        ((SignRendererAccessor) this).invoke$renderSignModel(poseStack, light, overlay, model, vertexconsumer);
        poseStack.popPose();
    }

    public record Provider<T>(
        Material modelMaterial,
        ResourceLocation textureLocation,
        T model
    ) {}
}
