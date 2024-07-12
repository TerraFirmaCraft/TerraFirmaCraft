/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import net.neoforged.neoforge.fluids.FluidType;

import net.dries007.tfc.common.fluids.TFCFluids;

public interface AquaticMob extends ILivingEntityExtension
{
    boolean canSpawnIn(Fluid fluid);

    @Override
    default boolean canSwimInFluidType(FluidType type)
    {
        return type == NeoForgeMod.WATER_TYPE.value() || type == TFCFluids.SALT_WATER.type().get() || type == TFCFluids.SPRING_WATER.type().get();
    }

    @Override
    default boolean canDrownInFluidType(FluidType type)
    {
        return !canSwimInFluidType(type);
    }

    /**
     * This allows our entities to assign "wasTouchingWater" properly
     */
    @Override
    default boolean isInFluidType(FluidType type)
    {
        if (!ILivingEntityExtension.super.isInFluidType(type) && type == NeoForgeMod.WATER_TYPE.value())
        {
            if (isInFluidType(TFCFluids.SALT_WATER.type().get()))
            {
                return true;
            }
            if (isInFluidType(TFCFluids.SPRING_WATER.type().get()))
            {
                return true;
            }
        }
        return ILivingEntityExtension.super.isInFluidType(type);
    }
}
