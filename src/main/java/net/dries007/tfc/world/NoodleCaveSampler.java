/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.List;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.NoiseInterpolator;
import net.minecraft.world.level.levelgen.NoodleCavifier;

public class NoodleCaveSampler
{
    private final NoodleCavifier noodleCavifier;

    private final NoiseInterpolator toggle;
    private final NoiseInterpolator thickness;
    private final NoiseInterpolator ridgeA;
    private final NoiseInterpolator ridgeB;

    public NoodleCaveSampler(NoodleCavifier noodleCavifier, ChunkPos chunkPos, int cellCountX, int cellCountY, int cellCountZ, int minCellY)
    {
        this.noodleCavifier = noodleCavifier;

        this.toggle = new NoiseInterpolator(cellCountX, cellCountY, cellCountZ, chunkPos, minCellY, noodleCavifier::fillToggleNoiseColumn);
        this.thickness = new NoiseInterpolator(cellCountX, cellCountY, cellCountZ, chunkPos, minCellY, noodleCavifier::fillThicknessNoiseColumn);
        this.ridgeA = new NoiseInterpolator(cellCountX, cellCountY, cellCountZ, chunkPos, minCellY, noodleCavifier::fillRidgeANoiseColumn);
        this.ridgeB = new NoiseInterpolator(cellCountX, cellCountY, cellCountZ, chunkPos, minCellY, noodleCavifier::fillRidgeBNoiseColumn);
    }

    public double sample(double noise, int x, int y, int z, int minY, double deltaZ)
    {
        double toggle = this.toggle.calculateValue(deltaZ);
        double thickness = this.thickness.calculateValue(deltaZ);
        double ridgeA = this.ridgeA.calculateValue(deltaZ);
        double ridgeB = this.ridgeB.calculateValue(deltaZ);
        return noodleCavifier.noodleCavify(noise, x, y, z, toggle, thickness, ridgeA, ridgeB, minY);
    }

    void addInterpolators(List<NoiseInterpolator> interpolators)
    {
        interpolators.add(toggle);
        interpolators.add(thickness);
        interpolators.add(ridgeA);
        interpolators.add(ridgeB);
    }
}
