/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Collection;
import java.util.List;
import com.mojang.datafixers.util.Either;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.util.SelfTests;

/**
 * Promote tag loading errors into full errors in a self test env. This targets the call to {@link TagLoader#build(TagEntry.Lookup, List)},
 * which returns an {@link Either#left()} if a tag loading error has occurred.
 * <p>
 * The slightly indirect targeting here is to work around having to target the exact error log message, which is patched by both Neo + Lex Forge
 * in newer versions (see <a href="#2638">https://github.com/TerraFirmaCraft/TerraFirmaCraft/pull/2638</a>), which makes it unreliable for us to
 * target the lambda method directly.
 */
@Mixin(TagLoader.class)
public abstract class TagLoaderMixin
{
    @Inject(method = "build(Lnet/minecraft/tags/TagEntry$Lookup;Ljava/util/List;)Lcom/mojang/datafixers/util/Either;", at = @At("RETURN"), require = 0)
    private <T> void setStateToErrored(TagEntry.Lookup<T> lookup, List<TagLoader.EntryWithSource> entries, CallbackInfoReturnable<Either<Collection<TagLoader.EntryWithSource>, Collection<T>>> cir)
    {
        if (cir.getReturnValue().left().isPresent()) SelfTests.reportExternalError();
    }
}
