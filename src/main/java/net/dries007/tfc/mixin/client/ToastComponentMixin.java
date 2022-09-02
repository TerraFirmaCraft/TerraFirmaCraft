/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.TutorialToast;

import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// todo: 1.19 remove this and use event
@Mixin(ToastComponent.class)
public abstract class ToastComponentMixin
{
    @Inject(method = "addToast", at = @At(value = "HEAD"), cancellable = true)
    private void inject$addToast(Toast toast, CallbackInfo ci)
    {
        if (!TFCConfig.CLIENT.enableVanillaTutorialToasts.get() && toast instanceof TutorialToast)
        {
            ci.cancel();
        }
    }
}
