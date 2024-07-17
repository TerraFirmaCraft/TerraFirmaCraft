/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
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
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.mixin.client.accessor.SignRendererAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class TFCHangingSignBlockEntityRenderer extends HangingSignRenderer
{
    private static final Map<Block, HangingSignModelData> RENDER_INFO = new HashMap<>();

    @Nullable
    public static HangingSignModelData getData(Block block)
    {
        return RENDER_INFO.get(block);
    }

    public static synchronized void registerData(Block block, HangingSignModelData modelData)
    {
        RENDER_INFO.put(block, modelData);
    }

    /**
     * Provided as a helper to avoid loading Material early.
     */
    public static HangingSignModelData createModelData(ResourceLocation signLocation, ResourceLocation guiLocation)
    {
        return new HangingSignModelData(new Material(Sheets.SIGN_SHEET, signLocation), guiLocation);
    }

    private static HangingSignModelData createModelData(Metal metal, Supplier<? extends SignBlock> reg)
    {
        final WoodType type = reg.get().type();
        final ResourceLocation woodName = Helpers.resourceLocation(type.name());
        final ResourceLocation metalName = Helpers.identifier(metal.getSerializedName());

        return createModelData(
            Helpers.resourceLocation(woodName.getNamespace(), "entity/signs/hanging/" + metalName.getPath() + "/" + woodName.getPath()),
            Helpers.resourceLocation(type.name() + ".png").withPrefix("textures/gui/hanging_signs/" + metalName.getPath() + "/")
        );
    }

    static
    {
        TFCBlocks.CEILING_HANGING_SIGNS.forEach((wood, map) -> map.forEach((metal, reg) -> RENDER_INFO.put(reg.get(), createModelData(metal, reg))));
        TFCBlocks.WALL_HANGING_SIGNS.forEach((wood, map) -> map.forEach((metal, reg) -> RENDER_INFO.put(reg.get(), createModelData(metal, reg))));
    }

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
            modelBuilder.put(data.type(), new HangingSignModel(context.bakeLayer(new ModelLayerLocation(Helpers.resourceLocation(data.domain(), "hanging_sign/" + data.name()), "main"))));
        });
        this.hangingSignModels = modelBuilder.build();
    }

    @Override
    public void render(SignBlockEntity sign, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        final BlockState state = sign.getBlockState();
        final SignBlock signBlock = (SignBlock) state.getBlock();
        final WoodType woodType = SignBlock.getWoodType(signBlock);
        final HangingSignRenderer.HangingSignModel model = this.hangingSignModels.get(woodType);
        final HangingSignModelData modelData = Objects.requireNonNull(getData(signBlock));

        model.evaluateVisibleParts(state);

        renderSignWithText(sign, poseStack, buffer, light, overlay, state, signBlock, modelData.modelMaterial(), model);
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

    public record HangingSignModelData(Material modelMaterial, ResourceLocation textureLocation) {}
}
