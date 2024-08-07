/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.mixin.client.accessor.SignRendererAccessor;
import net.dries007.tfc.util.Helpers;

public class TFCSignBlockEntityRenderer extends SignRenderer
{
    public static final Map<WoodType, Function<BlockEntityRendererProvider.Context, SignModel>> MODELS = RenderHelpers.mapOf(map -> {
        for (Wood wood : Wood.values())
            map.accept(
                wood::getVanillaWoodType,
                context -> new SignModel(context.bakeLayer(RenderHelpers.layerId("sign/" + wood.getSerializedName())))
            );
    });

    private final Map<WoodType, SignModel> signModels;

    public TFCSignBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        signModels = Helpers.mapValue(MODELS, f -> f.apply(context));
    }

    @Override
    public void render(SignBlockEntity sign, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight, int overlay)
    {
        BlockState blockstate = sign.getBlockState();
        SignBlock signblock = (SignBlock) blockstate.getBlock();
        WoodType woodType = SignBlock.getWoodType(signblock);
        SignRenderer.SignModel model = this.signModels.get(woodType);
        model.stick.visible = blockstate.getBlock() instanceof StandingSignBlock;
        ((SignRendererAccessor) this).invoke$renderSignWithText(sign, poseStack, source, packedLight, overlay, blockstate, signblock, woodType, model);
    }
}
