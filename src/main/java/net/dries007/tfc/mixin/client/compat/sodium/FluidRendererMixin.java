/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.compat.sodium;

import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.fluids.FluidHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Sodium replaces the use of {@link net.minecraft.client.renderer.block.LiquidBlockRenderer} entirely, which makes two of our mixins in {@link net.dries007.tfc.mixin.client.LiquidBlockRendererMixin} which support fluid mixing rendering behavior unable to work.
 * However, the sodium replacement has near 1-1 copies of the target methods - namely, we can retarget both of these mixins without introducing any dependency.
 */
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer", remap = false)
public abstract class FluidRendererMixin
{
    @Redirect(method = "isFluidOccluded(Lnet/minecraft/world/level/BlockAndTintGetter;IIILnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/Fluid;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/Fluid;isSame(Lnet/minecraft/world/level/material/Fluid;)Z"), remap = false, require = 0)
    private boolean isFluidOccludedWithMixing(Fluid fluid, Fluid fluidIn)
    {
        return fluid.isSame(fluidIn) || FluidHelpers.canMixFluids(fluid, fluidIn);
    }

    @Redirect(method = "fluidHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/Fluid;isSame(Lnet/minecraft/world/level/material/Fluid;)Z"), remap = false, require = 0)
    private boolean fluidHeightWithMixing(Fluid fluid, Fluid fluidIn)
    {
        return fluid.isSame(fluidIn) || FluidHelpers.canMixFluids(fluid, fluidIn);
    }
}
