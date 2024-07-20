/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.compat.sodium;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.config.TFCConfig;

/**
 * Priority after default, which should let us inject into the method that sodium adds.
 * <p>
 * This implements a default value in the new color accessor added by both Rubidium and Embeddium,
 * which bypasses vanilla's query entirely (and so our normal default mixin does not get used)
 * <ul>
 *     <li>Rubidium adds {@code getColorProvider} in <a href="https://github.com/Asek3/Rubidium/blob/1.20/dev/src/main/java/me/jellysquid/mods/sodium/mixin/core/model/MixinItemColors.java">{@code ItemColorsMixin}</a></li>
 *     <li>Embeddum adds {@code sodium$getColorProvider} in <a href="https://github.com/embeddedt/embeddium/blob/20.1/forge/src/main/java/me/jellysquid/mods/sodium/mixin/core/model/colors/ItemColorsMixin.java">{@code ItemColorsMixin}</a></li>
 * </ul>
 */
@Mixin(value = ItemColors.class, priority = 2000)
public abstract class ItemColorsMixin
{
    @Dynamic("Inject into method added by a Sodium mixin")
    @Inject(
        at = @At("TAIL"),
        target = {
            @Desc(value = "getColorProvider", args = {ItemStack.class}, ret = ItemColor.class), // Rubidium
            @Desc(value = "sodium$getColorProvider", args = {ItemStack.class}, ret = ItemColor.class) // Embeddium
        },
        cancellable = true,
        require = 0,
        remap = false
    )
    private void getColorProviderWithRottenFood(ItemStack stack, CallbackInfoReturnable<ItemColor> cir)
    {
        if (FoodCapability.isRotten(stack))
        {
            cir.setReturnValue((stackIn, tintIndex) -> TFCConfig.CLIENT.foodExpiryOverlayColor.get());
        }
    }
}
