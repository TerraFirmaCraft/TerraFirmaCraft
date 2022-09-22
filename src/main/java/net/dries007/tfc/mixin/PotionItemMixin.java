/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;

import net.dries007.tfc.util.Drinkable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin
{
    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void applyDrinkingEffects(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir)
    {
        Drinkable.drinkFromPotion(stack, level, entity);
    }
}
