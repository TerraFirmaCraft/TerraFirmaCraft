/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.util.palette;

import net.minecraft.util.palette.PalettedContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This disables locking on {@link PalettedContainer}, which speeds up calls to setBlockState during early world gen.
 * As we do a lot of those natively, this offers a decent performance boost, and reduces the need to interact directly with the {@link net.minecraft.world.chunk.ChunkSection} and explicitly disable locking.
 */
@Mixin(PalettedContainer.class)
public class PalettedContainerMixin
{
    @Inject(method = "acquire", at = @At("HEAD"), cancellable = true, require = 0)
    private void inject$aquire(CallbackInfo ci)
    {
        ci.cancel();
    }

    @Inject(method = "release", at = @At("HEAD"), cancellable = true, require = 0)
    private void inject$release(CallbackInfo ci)
    {
        ci.cancel();
    }
}
