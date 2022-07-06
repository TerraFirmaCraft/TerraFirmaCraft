/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Collection;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagLoader;

import net.dries007.tfc.util.SelfTests;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Promote tag loading errors into full errors in a self test env.
 */
@Mixin(TagLoader.class)
public abstract class TagLoaderMixin
{
    @Dynamic("Lambda in build(), the ifLeft() which logs an error")
    @Inject(method = "*(Lnet/minecraft/resources/ResourceLocation;Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false), require = 0)
    private static void setStateToErrored(ResourceLocation id, Collection<Tag.BuilderEntry> entries, CallbackInfo ci)
    {
        SelfTests.reportExternalTagLoadingErrors();
    }
}
