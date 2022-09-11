/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.compat.sodium;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.fluids.FluidHelpers;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Sodium replaces the use of {@link net.minecraft.client.renderer.block.LiquidBlockRenderer} entirely, which makes two of our mixins in {@link net.dries007.tfc.mixin.client.LiquidBlockRendererMixin} which support fluid mixing rendering behavior unable to work.
 * However, the sodium replacement has near 1-1 copies of the target methods - namely, we can retarget both of these mixins without introducing any dependency.
 */
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer")
public abstract class FluidRendererMixin
{
    @Dynamic("Method boolean isFluidOccluded(BlockAndTintGetter, int, int, Direction, Fluid) in Sodium's FluidRenderer")
    @Redirect(method = "isFluidOccluded", target = @Desc(value = "isFluidOccluded", args = {BlockAndTintGetter.class, int.class, int.class, int.class, Direction.class, Fluid.class}, ret = boolean.class), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/Fluid;isSame(Lnet/minecraft/world/level/material/Fluid;)Z"), require = 0)
    private boolean isFluidOccludedWithMixing(Fluid fluid, Fluid fluidIn)
    {
        return fluid.isSame(fluidIn) || FluidHelpers.canMixFluids(fluid, fluidIn);
    }

    @Dynamic("Method float fluidHeight(BlockAndTintGetter, Fluid, BlockPos) in Sodium's FluidRenderer")
    @Redirect(target = @Desc(value = "fluidHeight", args = {BlockAndTintGetter.class, Fluid.class, BlockPos.class}, ret = float.class), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/Fluid;isSame(Lnet/minecraft/world/level/material/Fluid;)Z"), require = 0)
    private boolean fluidHeightWithMixing(Fluid fluid, Fluid fluidIn)
    {
        return fluid.isSame(fluidIn) || FluidHelpers.canMixFluids(fluid, fluidIn);
    }
}
