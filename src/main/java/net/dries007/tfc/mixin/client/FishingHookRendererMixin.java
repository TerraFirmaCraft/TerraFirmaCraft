/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.renderer.entity.FishingHookRenderer;

import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * todo: 1.19. remove this
 */
@Mixin(FishingHookRenderer.class)
public abstract class FishingHookRendererMixin
{

    /**
     * Allows our fishing rods to masquerade as vanilla fishing rods when checking what hand they should render in.
     */
    @ModifyVariable(
        method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "LOAD", ordinal = 0),
        ordinal = 0,
        index = 13,
        name = "itemstack"
    )
    private ItemStack allowAnyFishingRods(ItemStack stack)
    {
        return stack.getItem() instanceof FishingRodItem ? new ItemStack(Items.FISHING_ROD) : stack;
    }
}
