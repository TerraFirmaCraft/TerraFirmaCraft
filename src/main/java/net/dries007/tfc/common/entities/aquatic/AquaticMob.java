/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import net.minecraftforge.fluids.FluidType;

import net.dries007.tfc.common.fluids.TFCFluids;

public interface AquaticMob extends IForgeLivingEntity
{
    boolean canSpawnIn(Fluid fluid);

    @Override
    default boolean canSwimInFluidType(FluidType type)
    {
        return type == ForgeMod.WATER_TYPE.get() || type == TFCFluids.SALT_WATER.type().get() || type == TFCFluids.SPRING_WATER.type().get();
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
        if (!IForgeLivingEntity.super.isInFluidType(type) && type == ForgeMod.WATER_TYPE.get())
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
        return IForgeLivingEntity.super.isInFluidType(type);
    }
}
