/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.List;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;

import com.mojang.serialization.Codec;

public class NoopClimateSampler
{
    private static final DensityFunction NONE = new DensityFunction.SimpleFunction() {
        @Override
        public double compute(FunctionContext ctx)
        {
            return 0;
        }

        @Override
        public double minValue()
        {
            return 0;
        }

        @Override
        public double maxValue()
        {
            return 0;
        }

        @Override
        public Codec<? extends DensityFunction> codec()
        {
            return Codec.unit(() -> NONE);
        }
    };
    public static final Climate.Sampler INSTANCE = new Climate.Sampler(NONE, NONE, NONE, NONE, NONE, NONE, List.of());
}
