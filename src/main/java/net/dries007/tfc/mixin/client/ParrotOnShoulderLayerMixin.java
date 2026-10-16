/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import java.util.Optional;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
        return tag.contains("species") ? TFCParrotRenderer.getTexture(TFCParrot.Species.byName(tag.getString("species"))) : texture;
    }
}