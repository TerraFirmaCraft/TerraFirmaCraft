/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    /**
     * Inject into the same spot where unbreaking enchantment is processed, in order to additionally apply forging bonus in the same respect
     */
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void applyForgingBonusToPreventItemDamage(int amount, Random random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir)
    {
        if (ForgingBonus.applyLikeUnbreaking((ItemStack) (Object) this, random))
        {
            cir.setReturnValue(false);
        }
    }
}
