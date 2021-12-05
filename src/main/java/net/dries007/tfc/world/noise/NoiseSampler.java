package net.dries007.tfc.world.noise;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.world.NoiseBasedAquifer;

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

    private static double getDiscreteSpaghettiRarity2D(double value)
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

    private static double getDiscreteSpaghettiRarity3D(double value)
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

    private final PositionalRandomFactory positionalRandomFactory;
    private final NoiseSettings noiseSettings;
    private final BlendedNoise blendedNoise;

    private final NormalNoise pillarNoiseSource;
    private final NormalNoise pillarRarenessModulator;
    private final NormalNoise pillarThicknessModulator;
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

    // Aquifers
    private final NormalNoise barrierNoise;
    private final NormalNoise fluidLevelFloodednessNoise;
    private final NormalNoise fluidLevelSpreadNoise;
    private final NormalNoise lavaNoise;

    public NoiseSampler(NoiseSettings noiseSettings, long seed, Registry<NormalNoise.NoiseParameters> parameters)
    {
        this.positionalRandomFactory = new XoroshiroRandomSource(seed).forkPositional();
        this.noiseSettings = noiseSettings;
        this.blendedNoise = new BlendedNoise(positionalRandomFactory.fromHashOf(new ResourceLocation("terrain")), noiseSettings.noiseSamplingSettings(), noiseSettings.getCellWidth(), noiseSettings.getCellHeight());

        // Noise Caves
        this.noiseCaves = this::calculateBaseNoise;
        this.pillarNoiseSource = Noises.instantiate(parameters, positionalRandomFactory, Noises.PILLAR);
        this.pillarRarenessModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.PILLAR_RARENESS);
        this.pillarThicknessModulator = Noises.instantiate(parameters, positionalRandomFactory, Noises.PILLAR_THICKNESS);
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
        this.fluidLevelFloodednessNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS);
        this.lavaNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_LAVA);
        this.fluidLevelSpreadNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_FLUID_LEVEL_SPREAD);
    }

    public NoiseBasedAquifer createAquifer(ChunkPos chunkPos, ChunkNoiseSamplingSettings settings, int seaLevel)
    {
        return new NoiseBasedAquifer(settings, chunkPos, barrierNoise, fluidLevelFloodednessNoise, fluidLevelSpreadNoise, lavaNoise, positionalRandomFactory, seaLevel);
    }

    private double calculateBaseNoise(int x, int y, int z)
    {
        double blendedNoiseValue = blendedNoise.calculateNoise(x, y, z);
        return this.calculateBaseNoise(x, y, z, blendedNoiseValue);
    }

    private double calculateBaseNoise(int x, int y, int z, double blendedNoiseValue)
    {
        double d0 = 0;
        // todo: in theory this is where the terrain noise gets input, but how?
        //d0 = d2 * (double) (d2 > 0.0D ? 4 : 1);

        double d16 = d0 + blendedNoiseValue;
        double d3;
        double d4;
        double d5;
        if (d16 < -64.0D)
        {
            d3 = d16;
            d4 = 64.0D;
            d5 = -64.0D;
        }
        else
        {
            double d6 = d16 - 1.5625D;
            boolean flag = d6 < 0.0D;
            double d7 = this.getBigEntrances(x, y, z);
            double d8 = this.spaghettiRoughness(x, y, z);
            double d9 = this.getSpaghetti3D(x, y, z);
            double d10 = Math.min(d7, d9 + d8);
            if (flag)
            {
                d3 = d16;
                d4 = d10 * 5.0D;
                d5 = -64.0D;
            }
            else
            {
                double d11 = this.getLayerizedCaverns(x, y, z);
                if (d11 > 64.0D)
                {
                    d3 = 64.0D;
                }
                else
                {
                    double d12 = this.cheeseNoiseSource.getValue(x, y / 1.5, z);
                    double d13 = Mth.clamp(d12 + 0.27D, -1.0D, 1.0D);
                    double d14 = d6 * 1.28D;
                    double d15 = d13 + Mth.clampedLerp(0.5D, 0.0D, d14);
                    d3 = d15 + d11;
                }

                double d19 = this.getSpaghetti2D(x, y, z);
                d4 = Math.min(d10, d19 + d8);
                d5 = this.getPillars(x, y, z);
            }
        }

        double value = Math.max(Math.min(d3, d4), d5);
        value = this.applySlide(value, y / noiseSettings.getCellHeight());
        // value = blender.blendDensity(x, y, z, value);
        return Mth.clamp(value, -64.0D, 64.0D);
    }

    protected double applySlide(double value, int cellY)
    {
        int absoluteCellY = cellY - noiseSettings.getMinCellY();
        value = noiseSettings.topSlideSettings().applySlide(value, noiseSettings.getCellCountY() - absoluteCellY);
        return noiseSettings.bottomSlideSettings().applySlide(value, absoluteCellY);
    }

    private double getBigEntrances(int x, int y, int z)
    {
        double d3 = bigEntranceNoiseSource.getValue(x * 0.75, y * 0.5, z * 0.75) + 0.37;
        double d4 = (y + 10) / 40d;
        return d3 + Mth.clampedLerp(0.3, 0, d4);
    }

    private double getPillars(int x, int y, int z)
    {
        double d2 = NoiseUtils.sampleNoiseAndMapToRange(pillarRarenessModulator, x, y, z, 0, 2);
        double d5 = NoiseUtils.sampleNoiseAndMapToRange(pillarThicknessModulator, x, y, z, 0, 1.1);
        d5 = Math.pow(d5, 3.0D);
        double d8 = pillarNoiseSource.getValue(x * 25.0D, y * 0.3D, z * 25);
        d8 = d5 * (d8 * 2.0D - d2);
        return d8 > 0.03D ? d8 : Double.NEGATIVE_INFINITY;
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
        double d4 = NoiseUtils.sampleNoiseAndMapToRange(spaghetti3DThicknessModulator, x, y, z, 0.065, 0.088);
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
        double d4 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2DThicknessModulator, x * 2, y, z * 2, 0.6, 1.3);
        double d5 = sampleWithRarity(this.spaghetti2DNoiseSource, x, y, z, d1);
        double d7 = Math.abs(d1 * d5) - 0.083D * d4;
        int i = this.noiseSettings.getMinCellY();
        double d8 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2DElevationModulator, x, 0, z, i, 8);
        double d9 = Math.abs(d8 - (double) y / 8.0D) - d4;
        d9 = d9 * d9 * d9;
        return clampToUnit(Math.max(d9, d7));
    }

    private double spaghettiRoughness(int x, int y, int z)
    {
        double value = NoiseUtils.sampleNoiseAndMapToRange(spaghettiRoughnessModulator, x, y, z, 0, 0.1);
        return (0.4 - Math.abs(spaghettiRoughnessNoise.getValue(x, y, z))) * value;
    }
}
