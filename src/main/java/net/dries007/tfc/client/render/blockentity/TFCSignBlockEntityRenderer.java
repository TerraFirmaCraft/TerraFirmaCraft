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
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.mixin.client.accessor.SignRendererAccessor;
import net.dries007.tfc.util.Helpers;

public class TFCSignBlockEntityRenderer extends SignRenderer
{
    private final Map<WoodType, SignModel> signModels;

    public TFCSignBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this(context, TFCBlocks.WOODS.keySet()
            .stream()
            .map(map -> new SignModelData(
                TerraFirmaCraft.MOD_ID,
                map.getSerializedName(),
                map.getVanillaWoodType()
            )));
    }

    public TFCSignBlockEntityRenderer(BlockEntityRendererProvider.Context context, Stream<SignModelData> blocks)
    {
        super(context);

        ImmutableMap.Builder<WoodType, SignModel> modelBuilder = ImmutableMap.builder();
        blocks.forEach(data -> {
            modelBuilder.put(data.type, new SignModel(context.bakeLayer(new ModelLayerLocation(Helpers.resourceLocation(data.domain, "sign/" + data.name), "main"))));
        });
        this.signModels = modelBuilder.build();
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

    public record SignModelData(String domain, String name, WoodType type) {}
}
