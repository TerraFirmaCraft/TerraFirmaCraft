/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Map;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Metal;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.mixin.client.accessor.SignRendererAccessor;

public class TFCHangingSignBlockEntityRenderer extends HangingSignRenderer
{
    private final Map<WoodType, HangingSignModel> hangingSignModels;

    public TFCHangingSignBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this(context, TFCBlocks.WOODS.keySet()
            .stream()
            .map(map -> new TFCSignBlockEntityRenderer.SignModelData(
                TerraFirmaCraft.MOD_ID,
                map.getSerializedName(),
                map.getVanillaWoodType()
            )));
    }

    public TFCHangingSignBlockEntityRenderer(BlockEntityRendererProvider.Context context, Stream<TFCSignBlockEntityRenderer.SignModelData> blocks)
    {
        super(context);

        ImmutableMap.Builder<WoodType, HangingSignModel> modelBuilder = ImmutableMap.builder();
        blocks.forEach(data -> {
            modelBuilder.put(data.type(), new HangingSignModel(context.bakeLayer(new ModelLayerLocation(new ResourceLocation(data.domain(), "hanging_sign/" + data.name()), "main"))));
        });
        this.hangingSignModels = modelBuilder.build();
    }

    @Override
    public void render(SignBlockEntity sign, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        final BlockState blockstate = sign.getBlockState();
        final SignBlock signblock = (SignBlock)blockstate.getBlock();
        final WoodType woodtype = SignBlock.getWoodType(signblock);

        // Placeholder - should determine the metal from the SignBlock instance.
        // TODO register TFCCeilingHangingSignBlock and TFCWallHangingSignBlock versions for every combination
        // for now just unknown so that we have something to pass.
        final Metal metal = Metal.unknown();

        final HangingSignRenderer.HangingSignModel model = this.hangingSignModels.get(woodtype);
        model.evaluateVisibleParts(blockstate);
        this.renderSignWithText(sign, poseStack, buffer, light, overlay, blockstate, signblock, woodtype, metal, model);
    }

    // behavior copied from SignRenderer#renderSignWithText
    void renderSignWithText(SignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, BlockState blockstate, SignBlock signblock, WoodType woodtype, Metal metal, Model model) {
        poseStack.pushPose();
        ((SignRendererAccessor) this).invoke$translateSign(poseStack, -signblock.getYRotationDegrees(blockstate), blockstate);
        this.renderSign(poseStack, buffer, light, overlay, woodtype, metal, model);
        ((SignRendererAccessor) this).invoke$renderSignText(sign.getBlockPos(), sign.getFrontText(), poseStack, buffer, light, sign.getTextLineHeight(), sign.getMaxTextLineWidth(), true);
        ((SignRendererAccessor) this).invoke$renderSignText(sign.getBlockPos(), sign.getBackText(), poseStack, buffer, light, sign.getTextLineHeight(), sign.getMaxTextLineWidth(), false);
        poseStack.popPose();
    }

    // behavior copied from SignRenderer#renderSign
    void renderSign(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, WoodType woodtype, Metal metal, Model model) {
        poseStack.pushPose();
        float f = this.getSignModelRenderScale();
        poseStack.scale(f, -f, -f);

        Material material = this.getCompoundSignMaterial(woodtype, metal);

        VertexConsumer vertexconsumer = material.buffer(buffer, model::renderType);
        ((SignRendererAccessor) this).invoke$renderSignModel(poseStack, light, overlay, model, vertexconsumer);
        poseStack.popPose();
    }

    private Material getCompoundSignMaterial(WoodType woodType, Metal metal) {
        // Placeholder - should use the provided woodType and metal to determine the material.
        // TODO register new sign materials somewhere so they can be looked up here
        // for now just use willow so that there's visual confirmation the mixins worked.
        return Sheets.getHangingSignMaterial(Wood.WILLOW.getVanillaWoodType());
    }


}
