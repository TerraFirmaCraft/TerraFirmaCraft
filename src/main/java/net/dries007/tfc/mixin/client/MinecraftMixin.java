/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Minecraft;

import net.dries007.tfc.util.SelfTests;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Dynamic("Lambda method in <init>, lambda$new$1")
    @Inject(method = "*(Ljava/lang/String;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/loading/ClientModLoader;completeModLoading()Z", remap = false), remap = false)
    private void runSelfTests(String s, int i, CallbackInfo ci)
    {
        SelfTests.runClientSelfTests();
    }
}
