/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.util.Helpers;


public class NoiseSampler
{
    private static double clampToUnit(double value)
    {
        return Mth.clamp(value, -1, 1);
    }

    private static double sampleWithRarity(NormalNoise noise, double x, double y, double z, double rarity)
    {
        return noise.getValue(x / rarity, y / rarity, z / rarity);
    }

    private static TrilinearInterpolator.Source clamped(NormalNoise normalNoise, int minY, int maxY, int outOfBoundsValue, double scale)
    {
        return (x, y, z) -> y <= maxY && y >= minY ? normalNoise.getValue(x * scale, y * scale, z * scale) : outOfBoundsValue;
    }

    // Noise Caves
    public final TrilinearInterpolator.Source noiseCaves;

    // Noodle Caves
    public final TrilinearInterpolator.Source noodleToggle;
    public final TrilinearInterpolator.Source noodleThickness;
    public final TrilinearInterpolator.Source noodleRidgeA;
    public final TrilinearInterpolator.Source noodleRidgeB;

    // Aquifers
    public final NormalNoise barrierNoise;

    public final PositionalRandomFactory positionalRandomFactory;

    private final NoiseSettings noiseSettings;

    private final NormalNoise spaghetti2DNoiseSource;
    private final NormalNoise spaghetti2DElevationModulator;
    private final NormalNoise spaghetti2DRarityModulator;
    private final NormalNoise spaghetti2DThicknessModulator;
    private final NormalNoise spaghetti3DNoiseSource1;
    private final NormalNoise spaghetti3DNoiseSource2;
    private final NormalNoise spaghetti3DRarityModulator;
    private final NormalNoise spaghetti3DThicknessModulator;
    private final NormalNoise spaghettiRoughnessNoise;
    private final NormalNoise spaghettiRoughnessModulator;
    private final NormalNoise bigEntranceNoiseSource;
    private final NormalNoise layerNoiseSource;
    private final NormalNoise cheeseNoiseSource;

    public NoiseSampler(NoiseSettings noiseSettings, long seed, Registry<NormalNoise.NoiseParameters> parameters)
    {
        this.positionalRandomFactory = new XoroshiroRandomSource(seed).forkPositional();
        this.noiseSettings = noiseSettings;

        // Noise Caves
        this.noiseCaves = this::calculateNoiseCaves;
        this.spaghetti2DNoiseSource = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_2D);
        this.spaghetti2DElevationModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_2D_ELEVATION);
        this.spaghetti2DRarityModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_2D_MODULATOR);
        this.spaghetti2DThicknessModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_2D_THICKNESS);
        this.spaghetti3DNoiseSource1 = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_3D_1);
        this.spaghetti3DNoiseSource2 = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_3D_2);
        this.spaghetti3DRarityModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_3D_RARITY);
        this.spaghetti3DThicknessModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_3D_THICKNESS);
        this.spaghettiRoughnessNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_ROUGHNESS);
        this.spaghettiRoughnessModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.SPAGHETTI_ROUGHNESS_MODULATOR);
        this.bigEntranceNoiseSource = Noises.instantiate(parameters, positionalRandomFactory, Noises.CAVE_ENTRANCE);
        this.layerNoiseSource = Noises.instantiate(parameters, positionalRandomFactory, Noises.CAVE_LAYER);
        this.cheeseNoiseSource = Noises.instantiate(parameters, positionalRandomFactory, Noises.CAVE_CHEESE);

        // Noodle Caves
        final int minWorldY = noiseSettings.minY();
        final int minLimitY = minWorldY + 4;
        final int maxLimitY = minWorldY + noiseSettings.height();

        this.noodleToggle = clamped(Noises.instantiate(parameters, positionalRandomFactory, Noises.NOODLE), minLimitY, maxLimitY, -1, 1);
        this.noodleThickness = clamped(Noises.instantiate(parameters, positionalRandomFactory, Noises.NOODLE_THICKNESS), minLimitY, maxLimitY, 0, 1);
        this.noodleRidgeA = clamped(Noises.instantiate(parameters, positionalRandomFactory, Noises.NOODLE_RIDGE_A), minLimitY, maxLimitY, 0, 2.6666666666666665);
        this.noodleRidgeB = clamped(Noises.instantiate(parameters, positionalRandomFactory, Noises.NOODLE_RIDGE_B), minLimitY, maxLimitY, 0, 2.6666666666666665);

        // Aquifer
        this.barrierNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_BARRIER);
    }

    /**
     * @return Noise values in [-64, 64], with negative values indicating air.
     */
    private double calculateNoiseCaves(int x, int y, int z)
    {
        final double bigEntrances = getBigEntrances(x, y, z);

        final double spaghettiRoughness = getSpaghettiRoughness(x, y, z);
        final double spaghetti2D = getSpaghetti2D(x, y, z);
        final double spaghetti3D = getSpaghetti3D(x, y, z);
        final double spaghetti = spaghettiRoughness + Math.min(spaghetti2D, spaghetti3D);

        final double cheese = Mth.clamp(cheeseNoiseSource.getValue(x, y / 1.5, z) + 0.27, -1, 1);
        final double layerizedCaverns = getLayerizedCaverns(x, y, z);

        final double noise = Math.min(cheese + layerizedCaverns, Math.min(spaghetti, bigEntrances));

        final double clamped = Mth.clamp(noise, -1, 1);
        return applySlide(clamped, y);
    }

    protected double applySlide(double noise, int y)
    {
        if (y >= 20)
        {
            double slideFactor = Mth.inverseLerp(y, 20, noiseSettings.minY() + noiseSettings.height()); // [0, 1], 1 = top of world
            return Mth.lerp(slideFactor, noise, 2.5);
        }
        return noise;
    }

    private double getBigEntrances(int x, int y, int z)
    {
        double d3 = bigEntranceNoiseSource.getValue(x * 0.75, y * 0.5, z * 0.75) + 0.37;
        double d4 = (y + 10) / 40d;
        return d3 + Mth.clampedLerp(0.3, 0, d4);
    }

    private double getLayerizedCaverns(int x, int y, int z)
    {
        double value = this.layerNoiseSource.getValue(x, y * 8, z);
        return Mth.square(value) * 4.0D;
    }

    private double getSpaghetti3D(int x, int y, int z)
    {
        double d0 = spaghetti3DRarityModulator.getValue(x * 2, y, z * 2);
        double d1 = getDiscreteSpaghettiRarity3D(d0);
        double d4 = Helpers.sampleNoiseAndMapToRange(spaghetti3DThicknessModulator, x, y, z, 0.065, 0.088);
        double d5 = sampleWithRarity(spaghetti3DNoiseSource1, x, y, z, d1);
        double d6 = Math.abs(d1 * d5) - d4;
        double d7 = sampleWithRarity(spaghetti3DNoiseSource2, x, y, z, d1);
        double d8 = Math.abs(d1 * d7) - d4;
        return clampToUnit(Math.max(d6, d8));
    }

    private double getSpaghetti2D(int x, int y, int z)
    {
        double d0 = this.spaghetti2DRarityModulator.getValue(x * 2, y, z * 2);
        double d1 = getDiscreteSpaghettiRarity2D(d0);
        double d4 = Helpers.sampleNoiseAndMapToRange(this.spaghetti2DThicknessModulator, x * 2, y, z * 2, 0.6, 1.3);
        double d5 = sampleWithRarity(this.spaghetti2DNoiseSource, x, y, z, d1);
        double d7 = Math.abs(d1 * d5) - 0.083D * d4;
        int i = this.noiseSettings.getMinCellY();
        double d8 = Helpers.sampleNoiseAndMapToRange(this.spaghetti2DElevationModulator, x, 0, z, i, 8);
        double d9 = Math.abs(d8 - (double) y / 8.0D) - d4;
        d9 = d9 * d9 * d9;
        return clampToUnit(Math.max(d9, d7));
    }

    private double getSpaghettiRoughness(int x, int y, int z)
    {
        double value = Helpers.sampleNoiseAndMapToRange(spaghettiRoughnessModulator, x, y, z, 0, 0.1);
        return (0.4 - Math.abs(spaghettiRoughnessNoise.getValue(x, y, z))) * value;
    }

    private double getDiscreteSpaghettiRarity2D(double value)
    {
        if (value < -0.75)
        {
            return 0.5;
        }
        else if (value < -0.5)
        {
            return 0.75;
        }
        else if (value < 0.5)
        {
            return 1;
        }
        else
        {
            return value < 0.75 ? 2 : 3;
        }
    }

    private double getDiscreteSpaghettiRarity3D(double value)
    {
        if (value < -0.5)
        {
            return 0.75;
        }
        else if (value < 0)
        {
            return 1;
        }
        else
        {
            return value < 0.5 ? 1.5 : 2;
        }
    }
}
