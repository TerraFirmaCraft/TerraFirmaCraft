/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.items.EmptyPanItem;
import net.dries007.tfc.common.items.PanItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin
{
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void inject$renderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swing, ItemStack stack, float equip, PoseStack poseStack, MultiBufferSource source, int combinedLight, CallbackInfo ci)
    {
        final Item item = stack.getItem();
        if (item instanceof PanItem || item instanceof EmptyPanItem)
        {
            poseStack.pushPose();
            ClientHelpers.renderTwoHandedItem(poseStack, source, combinedLight, pitch, equip, swing, stack);
            poseStack.popPose();
            ci.cancel();
        }
    }
}
