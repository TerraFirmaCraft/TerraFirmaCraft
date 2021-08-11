/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.fluids.FluidAttributes;

import net.dries007.tfc.client.TFCColors;

import net.minecraftforge.fluids.FluidAttributes.Builder;

public class SaltWaterAttributes extends FluidAttributes
{
    public SaltWaterAttributes(Builder builder, Fluid fluid)
    {
        super(builder, fluid);
    }

    @Override
    public int getColor(BlockAndTintGetter world, BlockPos pos)
    {
        return world.getBlockTint(pos, TFCColors.SALT_WATER) | TFCFluids.ALPHA_MASK;
    }
}
