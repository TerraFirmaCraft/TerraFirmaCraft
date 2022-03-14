/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin
{
    @Inject(method = "loadResources", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void inject$loadResources(ResourceManager mgr, RegistryAccess.Frozen access, Commands.CommandSelection source, int integer, Executor exec, Executor exec2, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir, ReloadableServerResources reloadableserverresources, List<PreparableReloadListener> listeners)
    {
        Helpers.insertBefore(listeners, Metal.MANAGER, reloadableserverresources.getRecipeManager());
    }
}
