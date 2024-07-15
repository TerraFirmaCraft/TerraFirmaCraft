/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.util.SelfTests;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(
        method = "lambda$new$7(Lnet/minecraft/client/Minecraft$GameLoadCookie;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ResourceLoadStateTracker;finishReload()V")
    )
    private void runSelfTests(CallbackInfo ci)
    {
        SelfTests.runClientSelfTests();
    }
}
