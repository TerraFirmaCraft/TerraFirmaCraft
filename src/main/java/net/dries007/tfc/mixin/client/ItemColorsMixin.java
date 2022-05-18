/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import java.util.Map;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IRegistryDelegate;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin
{
    @Shadow @Final private Map<IRegistryDelegate<Item>, ItemColor> itemColors;

    /**
     * Inject here as opposed to using the color registry as we want this to function for all rotten foods, not just our items.
     */
    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void injectColorHandlerForCapabilityItems(ItemStack stack, int tintIndex, CallbackInfoReturnable<Integer> cir)
    {
        // Only modify if the default color handler would not be used
        if (!itemColors.containsKey(stack.getItem().delegate))
        {
            // If so, then if we're a food and rotten, apply our color handler
            final IFood food = stack.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
            if (food != null && food.isRotten())
            {
                cir.setReturnValue(TFCConfig.CLIENT.foodExpiryOverlayColor.get());
            }
        }
    }
}
