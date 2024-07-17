/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.density.Spaghetti2D;
import net.dries007.tfc.world.density.Spaghetti3D;
import net.dries007.tfc.world.density.TFCDensityFunctions;

import static net.minecraft.world.level.levelgen.DensityFunctions.*;

public final class BuiltinDensityFunctions
{
    public static void load(BootstrapContext<DensityFunction> context)
    {
        final HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);
        final HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);

        final DensityFunction y = new DensityFunctions.HolderHolder(functions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, Helpers.identifierMC("y"))));

        context.register(TFCDensityFunctions.NOISE_CAVES, lerp(
            yClampedGradient(20, 320, 0.0, 1.0),
            min(
                add(
                    add(
                        constant(0.27),
                        noise(noises.getOrThrow(Noises.CAVE_CHEESE), 1.0 / 1.5)
                    ).clamp(-1.0, 1.0),
                    mul(
                        constant(4.0),
                        noise(noises.getOrThrow(Noises.CAVE_LAYER), 8.0).square()
                    )
                ),
                min(
                    add(
                        mul(
                            add(
                                constant(0.4),
                                mul(constant(-1.0), noise(noises.getOrThrow(Noises.SPAGHETTI_ROUGHNESS)).abs())
                            ),
                            mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, 0.1)
                        ),
                        min(
                            new Spaghetti2D(
                                noise(noises.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0),
                                mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, 0.6, 1.3),
                                mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 1.0, 0.0, -8.0, 8.0),
                                new DensityFunction.NoiseHolder(noises.getOrThrow(Noises.SPAGHETTI_2D))
                            ),
                            new Spaghetti3D(
                                noise(noises.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0),
                                mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), 1.0, 1.0, 0.065, 0.088),
                                new DensityFunction.NoiseHolder(noises.getOrThrow(Noises.SPAGHETTI_3D_1)),
                                new DensityFunction.NoiseHolder(noises.getOrThrow(Noises.SPAGHETTI_3D_2))
                            )
                        )
                    ),
                    add(
                        yClampedGradient(-10, 30, 0.3, 0.0),
                        add(
                            constant(0.37),
                            noise(noises.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5)
                        )
                    )
                )
            ).clamp(-1.0, 1.0),
            constant(2.5)
        ));

        context.register(TFCDensityFunctions.NOODLE_TOGGLE, rangeChoice(
            y, -60, 320,
            noise(noises.getOrThrow(Noises.NOODLE)),
            constant(-1)
        ));
        context.register(TFCDensityFunctions.NOODLE_THICKNESS, rangeChoice(
            y, -60, 320,
            noise(noises.getOrThrow(Noises.NOODLE_THICKNESS)),
            constant(0)
        ));
        context.register(TFCDensityFunctions.NOODLE_RIDGE_A, rangeChoice(
            y, -60, 320,
            noise(noises.getOrThrow(Noises.NOODLE_RIDGE_A), 1.0 / 0.375, 1.0 / 0.375),
            constant(1)
        ));
        context.register(TFCDensityFunctions.NOODLE_RIDGE_B, rangeChoice(
            y, -60, 320,
            noise(noises.getOrThrow(Noises.NOODLE_RIDGE_B), 1.0 / 0.375, 1.0 / 0.375),
            constant(1)
        ));
    }
}
