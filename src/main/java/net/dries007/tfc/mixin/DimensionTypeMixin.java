/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;

@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin
{

    @Inject(method = "moonPhase", at = @At("HEAD"), cancellable = true)
    private void inject$getMoonPhase(long dayTime, CallbackInfoReturnable<Integer> cir)
    {
        if (TFCConfig.SERVER.enableCalendarSensitiveMoonPhases.get())
        {
            final int phase = Mth.floor(Mth.map(Calendars.get().getCalendarFractionOfMonth(), 0f, 1f, 0f, 8f));
            cir.setReturnValue(phase);
        }
    }


}
