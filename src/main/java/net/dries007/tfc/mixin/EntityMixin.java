/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow public abstract FluidType getEyeInFluidType();

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
        if ((Object) this instanceof LivingEntity living)
        {
            Helpers.slowEntityInsideBlocks(living);
        }
    }


    @Inject(method = "isEyeInFluid", at = @At("HEAD"), cancellable = true)
    private void nukeNeoforgeFluidTypeTag(TagKey<Fluid> fluidTagKey, CallbackInfoReturnable<Boolean> cir)
    {
        if (fluidTagKey == FluidTags.WATER)
        {
            final FluidType eyeInFluidType = getEyeInFluidType();
            if (eyeInFluidType == TFCFluids.SALT_WATER.getType() || eyeInFluidType == TFCFluids.SPRING_WATER.getType())
            {
                cir.setReturnValue(true);
            }
        }
    }

}
