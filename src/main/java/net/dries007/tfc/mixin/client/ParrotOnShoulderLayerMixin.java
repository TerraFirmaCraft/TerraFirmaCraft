/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import java.util.Optional;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.dries007.tfc.client.model.entity.TFCParrotModel;
import net.dries007.tfc.client.render.entity.TFCParrotRenderer;
import net.dries007.tfc.common.entities.prey.TFCParrot;

@Mixin(ParrotOnShoulderLayer.class)
public abstract class ParrotOnShoulderLayerMixin
{
    @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;byString(Ljava/lang/String;)Ljava/util/Optional;"))
    private Optional<EntityType<?>> renderTFCParrotOnShoulder(Optional<EntityType<?>> type)
    {
        return TFCParrot.asVanillaParrot(type);
    }

    @ModifyExpressionValue(method = "lambda$render$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ParrotRenderer;getVariantTexture(Lnet/minecraft/world/entity/animal/Parrot$Variant;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation useTFCParrotSpeciesTexture(ResourceLocation texture, @Local(argsOnly = true) CompoundTag tag)
    {
        return TFCParrot.isTFCParrot(tag) ? TFCParrotRenderer.getTexture(TFCParrot.Species.byName(tag.getString("species"))) : texture;
    }

    @WrapOperation(method = "lambda$render$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ParrotModel;renderOnShoulder(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFFI)V"))
    private void renderTFCParrotIdleOnShoulder(ParrotModel model, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, int tickCount, Operation<Void> original, @Local(argsOnly = true) CompoundTag tag, @Local(argsOnly = true) Player player, @Local(argsOnly = true) boolean leftShoulder)
    {
        if (TFCParrot.isTFCParrot(tag))
        {
            TFCParrotModel.renderOnShoulder(model, poseStack, buffer, packedLight, packedOverlay, limbSwing, limbSwingAmount, netHeadYaw, headPitch, player, tag, leftShoulder);
        }
        else
        {
            original.call(model, poseStack, buffer, packedLight, packedOverlay, limbSwing, limbSwingAmount, netHeadYaw, headPitch, tickCount);
        }
    }
}