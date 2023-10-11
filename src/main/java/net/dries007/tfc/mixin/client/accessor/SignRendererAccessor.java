/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.util.Metal;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SignRenderer.class)
public interface SignRendererAccessor
{
    @Invoker("renderSignWithText")
    void invoke$renderSignWithText(SignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, BlockState state, SignBlock block, WoodType wood, Model model);

    @Invoker("translateSign")
    void invoke$translateSign(PoseStack poseStack, float rotationDegrees, BlockState blockState);

    @Invoker("renderSignText")
    void invoke$renderSignText(BlockPos blockPos, SignText signText, PoseStack poseStack, MultiBufferSource buffer, int light, int lineHeight, int lineWidth, boolean mirrored);

    @Invoker("renderSignModel")
    void invoke$renderSignModel(PoseStack poseStack, int light, int overlay, Model model, VertexConsumer vertexconsumer);
}
