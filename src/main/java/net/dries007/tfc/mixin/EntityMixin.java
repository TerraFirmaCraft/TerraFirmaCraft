/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Inject(method = "updateFluidHeightAndDoFluidPushing(Lnet/minecraft/tags/TagKey;D)Z", at = @At("RETURN"), cancellable = true)
    private void fixUpdateFluidPushingToTreatWaterLikeFluidsAsWater(TagKey<Fluid> fluidTag, double motionScale, CallbackInfoReturnable<Boolean> cir)
    {
        if (fluidTag == TFCTags.Fluids.WATER_LIKE)
        {
            // Returning true here causes the one call site to set the entity's "is in water" flag
            cir.setReturnValue(cir.getReturnValueZ() || FluidHelpers.isInWaterLikeFluid((Entity) (Object) this));
        }
    }

    @Inject(method = "isEyeInFluid", at = @At("RETURN"), cancellable = true)
    private void fixIsEyeInFluidToTreatWaterLikeFluidsAsWater(TagKey<Fluid> fluidTag, CallbackInfoReturnable<Boolean> cir)
    {
        if (fluidTag == TFCTags.Fluids.WATER_LIKE)
        {
            // Returning true here causes the one call site to set the entity's "is in water" flag
            cir.setReturnValue(cir.getReturnValueZ() || FluidHelpers.isEyeInWaterLikeFluid((Entity) (Object) this));
        }
    }

    @Inject(method = "checkInsideBlocks", at = @At("HEAD"))
    private void checkInsideBlocksForCustomSlowEffects(CallbackInfo ci)
    {
        Helpers.slowEntityInsideBlocks((Entity) (Object) this);
    }
}
