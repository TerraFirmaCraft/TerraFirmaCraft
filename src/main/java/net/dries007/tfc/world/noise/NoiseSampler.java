/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.world.density.TFCDensityFunctions;


public final class NoiseSampler
{
    public final DensityFunction noiseCaves;
    public final DensityFunction noodleToggle;
    public final DensityFunction noodleThickness;
    public final DensityFunction noodleRidgeA;
    public final DensityFunction noodleRidgeB;

    // Aquifers
    public final NormalNoise barrierNoise;
    public final PositionalRandomFactory positionalRandomFactory;

    public NoiseSampler(long seed, HolderGetter<NormalNoise.NoiseParameters> parameters, HolderGetter<DensityFunction> functions)
    {
        this.positionalRandomFactory = new XoroshiroRandomSource(seed).forkPositional();

        this.noiseCaves = create(functions, TFCDensityFunctions.NOISE_CAVES);
        this.noodleToggle = create(functions, TFCDensityFunctions.NOODLE_TOGGLE);
        this.noodleThickness = create(functions, TFCDensityFunctions.NOODLE_THICKNESS);
        this.noodleRidgeA = create(functions, TFCDensityFunctions.NOODLE_RIDGE_A);
        this.noodleRidgeB = create(functions, TFCDensityFunctions.NOODLE_RIDGE_B);
        this.barrierNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_BARRIER);
    }

    private DensityFunction create(HolderGetter<DensityFunction> functions, ResourceKey<DensityFunction> key)
    {
        return functions.getOrThrow(key).value().mapAll(new NoiseWiringVisitor());
    }

    class NoiseWiringVisitor implements DensityFunction.Visitor
    {
        @Override
        public DensityFunction apply(DensityFunction f)
        {
            return f;
        }

        @Override
        public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder holder)
        {
            final NormalNoise noise = NormalNoise.create(positionalRandomFactory.fromHashOf(holder.noiseData().unwrapKey().orElseThrow().location()), holder.noiseData().value());
            return new DensityFunction.NoiseHolder(holder.noiseData(), noise);
        }
    }
}
