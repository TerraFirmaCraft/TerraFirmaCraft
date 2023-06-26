/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.client.Minecraft;

import net.dries007.tfc.util.SelfTests;

import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{

    @Dynamic("Lambda method in <init>, lambda$new$4")
    @Inject(method = "*(Lcom/mojang/realmsclient/client/RealmsClient;Lnet/minecraft/server/packs/resources/ReloadInstance;Lnet/minecraft/client/main/GameConfig;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/loading/ClientModLoader;completeModLoading()Z", remap = false), remap = false)
    private void runSelfTests(RealmsClient rc, ReloadInstance reloadInstance, GameConfig config, CallbackInfo ci)
    {
        SelfTests.runClientSelfTests();
    }
}
