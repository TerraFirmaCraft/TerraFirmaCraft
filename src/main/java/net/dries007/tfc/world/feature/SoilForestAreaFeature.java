/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class SoilForestAreaFeature extends Feature<SoilForestAreaConfig>
{
    public static final Noise2D noise = new OpenSimplex2D(913703L);

    public SoilForestAreaFeature(Codec<SoilForestAreaConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SoilForestAreaConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final ChunkAccess chunk = level.getChunk(pos);
        final SoilForestAreaConfig config = context.config();

        // Sum forest densities of adjacent chunks
        final ChunkPos chunkPos = new ChunkPos(pos);
        final int densityCenter = ChunkData.get(level, chunkPos).getForestType().getDensity();
        final int densityNorth = ChunkData.get(level, pos.north(16)).getForestType().getDensity();
        final int densitySouth = ChunkData.get(level, pos.south(16)).getForestType().getDensity();
        final int densityEast = ChunkData.get(level, pos.east(16)).getForestType().getDensity();
        final int densityWest = ChunkData.get(level, pos.west(16)).getForestType().getDensity();

        // Sum 4 chunks adjacent to each corner of this chunk
        final double density00 = 0.25 * (densityCenter + densityWest + densityNorth + ChunkData.get(level, pos.offset(-16, 0, -16)).getForestType().getDensity());
        final double density10 = 0.25 * (densityCenter + densityEast + densityNorth + ChunkData.get(level, pos.offset(16, 0, -16)).getForestType().getDensity());
        final double density01 = 0.25 * (densityCenter + densityWest + densitySouth + ChunkData.get(level, pos.offset(-16, 0, 16)).getForestType().getDensity());
        final double density11 = 0.25 * (densityCenter + densityEast + densitySouth + ChunkData.get(level, pos.offset(16, 0, 16)).getForestType().getDensity());

        // Coefficients for a smooth polynomial between corner intensities
        // Got this equation form from here: https://www.youtube.com/watch?v=BFld4EBO2RE&t=170s
        // But it's also a pretty standard math thingy
        final double deltaX = density10 - density00;
        final double deltaZ = density01 - density00;
        final double deltaXZ = density00 - density10 - density01 + density11;

        final BlockPos pos00 = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        boolean placed = false;
        for (int dx = -3; dx <= 18; dx++)
        {
            final int dxc = Math.clamp(dx, 0, 15);
            final double varDX = polynomialHelper(dxc * (1.0 / 15));
            final double termDX = deltaX * varDX;
            for (int dz = -3; dz <= 18; dz++)
            {
                final int dzc = Math.clamp(dz, 0, 15);

                // Evaluate polynomial
                final double varDZ = polynomialHelper(dzc * (1.0 / 15));
                final double forestDensity = density00 + termDX + deltaZ * varDZ + deltaXZ * varDX * varDZ;

                final int x = pos00.getX() + dx;
                final int z = pos00.getZ() + dz;
                final double blobNoise = noise.noise(x * config.spread(), z * config.spread()) * config.noiseScale();
                double soilDensity = Mth.clampedMap(forestDensity + blobNoise, config.minForest() - 0.25, config.maxForest() + 0.25, 0, 1);

                // Flip so that below this point, positive values are where soil gets placed
                if (config.inverted())
                {
                    soilDensity = 1 - soilDensity;
                }

                // If outside the chunk, we place some soil based on the density at the edge of the chunk
                if (dxc != dx || dzc != dz)
                {
                    final double edgeBlobNoise = (0.5 - Math.abs(noise.noise(x, z)));
                    if (dxc != dx)
                    {
                        soilDensity = Mth.clampedMap(Math.abs(dxc - dx), 0, 4, soilDensity, edgeBlobNoise);
                    }
                    if (dzc != dz)
                    {
                        soilDensity = Mth.clampedMap(Math.abs(dzc - dz), 0, 4, soilDensity, edgeBlobNoise);
                    }
                }

                if (soilDensity >= 0.5)
                {
                    placed |= placeColumn(chunk, config, level, x, z, mutablePos);
                }
            }
        }

        return placed;
    }

    public boolean placeColumn(ChunkAccess chunk, SoilForestAreaConfig config, WorldGenLevel level, int x, int z, BlockPos.MutableBlockPos mutablePos)
    {
        final int surfaceY = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        boolean placed = false;
        mutablePos.set(x, 0, z);
        for (int y = surfaceY - config.getHeight(); y <= surfaceY; ++y)
        {
            mutablePos.setY(y);

            final BlockState stateAt = level.getBlockState(mutablePos);
            final BlockState stateReplacement = config.getState(stateAt);
            if (stateReplacement != null)
            {
                level.setBlock(mutablePos, stateReplacement, 2);
                placed = true;
            }
        }
        return placed;
    }

    public static double polynomialHelper(double x)
    {
        return 3 * x * x - 2 * x * x * x;
    }
}
