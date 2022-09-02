/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// todo: 1.19 remove this and use event
@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin
{
    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;"), ordinal = 2)
    private int inject$renderEffects(int x)
    {
        if (!ClientHelpers.getPlayerOrThrow().isCreative())
        {
            return x + TFCConfig.CLIENT.effectHorizontalAdjustment.get();
        }
        return x;
    }
}
