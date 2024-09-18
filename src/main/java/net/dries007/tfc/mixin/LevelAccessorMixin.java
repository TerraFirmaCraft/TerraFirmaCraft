/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.client.overworld.ClientSolarCalculatorBridge;

@Mixin(LevelAccessor.class)
public interface LevelAccessorMixin
{
    /**
     * Redirects the default implementation of {@code dayTime()} to one that queries the client side solar-adjusted day time
     */
    @Inject(method = "dayTime", at = @At("HEAD"), cancellable = true)
    private void getSolarAdjustedDayTimeOnClient(CallbackInfoReturnable<Long> cir)
    {
        final LevelAccessor level = (LevelAccessor) this;
        if (level.isClientSide())
        {
            cir.setReturnValue(ClientSolarCalculatorBridge.getDayTime(level));
        }
    }
}
