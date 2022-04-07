/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

import org.apache.commons.lang3.mutable.MutableDouble;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.noise.Cellular3D;
import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;

public class TFCAquifer implements Aquifer
{
    private static final int GRID_WIDTH = 16;
    private static final int GRID_HEIGHT = 12;

    private static final int XZ_RANGE = 10;
    private static final int Y_RANGE = 8;

    private static final double FLOWING_UPDATE_SIMILARITY = similarity(Mth.square(10), Mth.square(12));

    private static int gridXZ(int xz)
    {
        return Math.floorDiv(xz, GRID_WIDTH);
    }

    private static int gridY(int y)
    {
        return Math.floorDiv(y, GRID_HEIGHT);
    }

    /**
     * When the distances are very similar, returns values close to 1
     * When the distances are very different, returns values < 1
     */
    private static double similarity(int firstDistance, int secondDistance)
    {
        return 1 - Math.abs(secondDistance - firstDistance) / 25d;
    }

    private final int minGridX, minGridY, minGridZ;
    private final int gridSizeX, gridSizeZ;
    private final int minChunkX, minChunkZ;
    private final int minY;
    private final PositionalRandomFactory fork;

    private final ChunkBaseBlockSource baseBlockSource;

    private final NormalNoise barrierNoise;
    private final long fluidCellSeed;
    private final Cellular3D fluidCellNoise;

    private final AquiferEntry lavaLevelAquifer;
    private final AquiferEntry seaLevelAquifer;

    private final AquiferEntry[] aquifers;
    private final long[] aquiferLocations;

    private int[] surfaceHeights;
    private boolean shouldScheduleFluidUpdate;

    public TFCAquifer(ChunkPos chunkPos, ChunkNoiseSamplingSettings settings, ChunkBaseBlockSource baseBlockSource, int seaLevel, PositionalRandomFactory fork, NormalNoise barrierNoise)
    {
        final int maxGridX = gridXZ(chunkPos.getMaxBlockX()) + 1;
        final int maxGridY = gridY((settings.firstCellY() + settings.cellCountY()) * settings.cellHeight()) + 1;
        final int maxGridZ = gridXZ(chunkPos.getMaxBlockZ()) + 1;

        this.minGridX = gridXZ(chunkPos.getMinBlockX()) - 1;
        this.minGridY = gridY(settings.firstCellY() * settings.cellHeight()) - 1;
        this.minGridZ = gridXZ(chunkPos.getMinBlockZ()) - 1;

        this.gridSizeX = maxGridX - minGridX + 1;
        final int gridSizeY = maxGridY - minGridY + 1;
        this.gridSizeZ = maxGridZ - minGridZ + 1;

        this.minChunkX = chunkPos.getMinBlockX();
        this.minY = settings.minY();
        this.minChunkZ = chunkPos.getMinBlockZ();

        this.surfaceHeights = new int[4 * 4];
        this.baseBlockSource = baseBlockSource;

        this.fork = fork;
        this.barrierNoise = barrierNoise;

        final RandomSource fluidCellNoiseFork = fork.fromHashOf("aquifer_fluid_cell_noise");
        this.fluidCellSeed = fluidCellNoiseFork.nextLong();
        this.fluidCellNoise =  new Cellular3D(fluidCellNoiseFork.nextLong()).spread(0.015f);

        this.lavaLevelAquifer = new AquiferEntry(Blocks.LAVA.defaultBlockState(), minY + 10);
        this.seaLevelAquifer = new AquiferEntry(Blocks.WATER.defaultBlockState(), seaLevel);

        this.aquifers = new AquiferEntry[gridSizeX * gridSizeY * gridSizeZ];
        this.aquiferLocations = new long[gridSizeX * gridSizeY * gridSizeZ];

        Arrays.fill(aquiferLocations, Long.MAX_VALUE);
    }

    public int[] getSurfaceHeights()
    {
        return surfaceHeights;
    }

    public void setSurfaceHeights(int[] surfaceHeights)
    {
        this.surfaceHeights = surfaceHeights;
    }

    @Nullable
    @Override
    public BlockState computeSubstance(DensityFunction.FunctionContext context, double baseNoise)
    {
        // Only used directly by carvers, where it passes in baseNoise = 0, modifiedNoise = 0
        return sampleState(context.blockX(), context.blockY(), context.blockZ(), baseNoise);
    }

    /**
     * @param terrainNoise Terrain density, where positive values indicate solid blocks.
     */
    @Nullable
    public BlockState sampleState(int x, int y, int z, double terrainNoise)
    {
        // Noise values < 0 indicate air, > 0 indicate solid blocks.
        if (terrainNoise <= 0)
        {
            // < 0 would generate air block (and we checked the modified noise, including contributions from caves)
            final AquiferEntry global = globalAquifer(y);

            double aquiferNoiseContribution; // The contribution from aquifer borders to the noise
            BlockState state; // The result aquifer state
            boolean isSurfaceLevelAquifer; // If the aquifer is a surface/sea level one, which needs to be affected by the water type

            if (Helpers.isBlock(global.at(y), Blocks.LAVA))
            {
                // Always lava below lava level, and don't generate adjacent borders.
                state = Blocks.LAVA.defaultBlockState();
                aquiferNoiseContribution = 0;
                isSurfaceLevelAquifer = false;
                shouldScheduleFluidUpdate = false;
            }
            else
            {
                final int lowerGridX = Math.floorDiv(x - XZ_RANGE / 2, GRID_WIDTH);
                final int lowerGridY = Math.floorDiv(y, GRID_HEIGHT);
                final int lowerGridZ = Math.floorDiv(z - XZ_RANGE / 2, GRID_WIDTH);

                // The closest three aquifers, by distance
                int distance1 = Integer.MAX_VALUE, distance2 = Integer.MAX_VALUE, distance3 = Integer.MAX_VALUE;
                long aquifer1 = 0L, aquifer2 = 0L, aquifer3 = 0L;

                // Iterate nearby aquifers
                for (int offsetGridX = 0; offsetGridX <= 1; ++offsetGridX)
                {
                    for (int offsetGridY = -1; offsetGridY <= 1; ++offsetGridY)
                    {
                        for (int offsetGridZ = 0; offsetGridZ <= 1; ++offsetGridZ)
                        {
                            final int adjGridX = lowerGridX + offsetGridX;
                            final int adjGridY = lowerGridY + offsetGridY;
                            final int adjGridZ = lowerGridZ + offsetGridZ;
                            final int adjIndex = getIndex(adjGridX, adjGridY, adjGridZ);

                            long adjAquifer = aquiferLocations[adjIndex];
                            if (adjAquifer == Long.MAX_VALUE)
                            {
                                // Compute and cache the aquifer location at this index
                                final RandomSource random = fork.at(adjGridX, adjGridY, adjGridZ);
                                adjAquifer = BlockPos.asLong(
                                    adjGridX * GRID_WIDTH + random.nextInt(XZ_RANGE) + (GRID_WIDTH - XZ_RANGE) / 2,
                                    adjGridY * GRID_HEIGHT + random.nextInt(Y_RANGE) + (GRID_HEIGHT - Y_RANGE) / 2,
                                    adjGridZ * GRID_WIDTH + random.nextInt(XZ_RANGE) + (GRID_WIDTH - XZ_RANGE) / 2);
                                aquiferLocations[adjIndex] = adjAquifer;
                            }

                            final int dx = BlockPos.getX(adjAquifer) - x;
                            final int dy = BlockPos.getY(adjAquifer) - y;
                            final int dz = BlockPos.getZ(adjAquifer) - z;
                            final int distance = dx * dx + dy * dy + dz * dz;

                            // Update the closest three aquifers
                            if (distance <= distance1)
                            {
                                aquifer3 = aquifer2;
                                aquifer2 = aquifer1;
                                aquifer1 = adjAquifer;

                                distance3 = distance2;
                                distance2 = distance1;
                                distance1 = distance;
                            }
                            else if (distance <= distance2)
                            {
                                aquifer3 = aquifer2;
                                aquifer2 = adjAquifer;

                                distance3 = distance2;
                                distance2 = distance;
                            }
                            else if (distance <= distance3)
                            {
                                aquifer3 = adjAquifer;
                                distance3 = distance;
                            }
                        }
                    }
                }

                // The status of the closest three aquifers (1, 2, 3 in order)
                final AquiferEntry entry1 = getOrCreateAquifer(aquifer1);
                final AquiferEntry entry2 = getOrCreateAquifer(aquifer2);
                final AquiferEntry entry3 = getOrCreateAquifer(aquifer3);

                // Similarity between each pair of aquifers
                final double similarity12 = similarity(distance1, distance2);
                final double similarity13 = similarity(distance1, distance3);
                final double similarity23 = similarity(distance2, distance3);

                if (Helpers.isBlock(entry1.at(y), Blocks.WATER) && Helpers.isBlock(globalAquifer(y - 1).at(y - 1), Blocks.LAVA))
                {
                    // Border lava and water with solid blocks.
                    aquiferNoiseContribution = 1;
                }
                else if (similarity12 > -1)
                {
                    final MutableDouble barrierNoise = new MutableDouble(Double.NaN);

                    // Pressure between each pair of aquifers
                    final double pressure12 = calculatePressure(x, y, z, barrierNoise, entry1, entry2);
                    final double pressure13 = calculatePressure(x, y, z, barrierNoise, entry1, entry3);
                    final double pressure23 = calculatePressure(x, y, z, barrierNoise, entry2, entry3);

                    // Clamped to [0, 1]
                    // When the aquifers are equidistant apart, similarity will be close to 1
                    final double clampedSimilarity12 = Math.max(0, similarity12);
                    final double clampedSimilarity13 = Math.max(0, similarity13);
                    final double clampedSimilarity23 = Math.max(0, similarity23);

                    // Magic formula. Mojang how did you come up with this
                    aquiferNoiseContribution = Math.max(0, 2 * clampedSimilarity12 * Math.max(pressure12, Math.max(pressure13 * clampedSimilarity13, pressure23 * clampedSimilarity23)));
                }
                else
                {
                    aquiferNoiseContribution = 0;
                }

                state = entry1.at(y);
                isSurfaceLevelAquifer = entry1.fluidY == TFCChunkGenerator.SEA_LEVEL_Y;
                shouldScheduleFluidUpdate = similarity12 >= FLOWING_UPDATE_SIMILARITY; // Heuristic if we should schedule a fluid update
            }

            if (terrainNoise + aquiferNoiseContribution <= 0)
            {
                if (isSurfaceLevelAquifer)
                {
                    state = baseBlockSource.modifyFluid(state, x, z);
                }
                return state;
            }
        }
        this.shouldScheduleFluidUpdate = false;
        return null;
    }

    public boolean shouldScheduleFluidUpdate()
    {
        return shouldScheduleFluidUpdate;
    }

    private double calculatePressure(int x, int y, int z, MutableDouble barrierNoise, AquiferEntry leftAquifer, AquiferEntry rightAquifer)
    {
        final BlockState leftState = leftAquifer.at(y);
        final BlockState rightState = rightAquifer.at(y);

        if ((Helpers.isBlock(leftState, Blocks.LAVA) && Helpers.isBlock(rightState, Blocks.WATER)) || (Helpers.isBlock(leftState, Blocks.WATER) && Helpers.isBlock(rightState, Blocks.LAVA)))
        {
            // Pressure between different fluid types aquifers is always one (maximum).
            return 1;
        }

        final int deltaFluidLevel = Math.abs(leftAquifer.fluidY - rightAquifer.fluidY);
        if (deltaFluidLevel == 0)
        {
            // Pressure between aquifers at the same fluid level (height) is zero.
            return 0;
        }

        final double averageFluidLevel = 0.5 * (leftAquifer.fluidY + rightAquifer.fluidY);
        final double deltaAboveAverageFluidLevel = y + 0.5 - averageFluidLevel;
        final double deltaNearAverageFluidLevel = (0.5 * deltaFluidLevel) - Math.abs(deltaAboveAverageFluidLevel);

        double pressure;
        if (deltaAboveAverageFluidLevel > 0)
        {
            pressure = deltaNearAverageFluidLevel / (deltaNearAverageFluidLevel > 0 ? 1.5 : 2.5);
        }
        else
        {
            pressure = (deltaNearAverageFluidLevel + 3) / (deltaNearAverageFluidLevel > -3 ? 3 : 10);
        }

        if (pressure < -2 || pressure > 2)
        {
            // Pressure is bounded between [-2, 2]
            return pressure;
        }

        final double cachedBarrierNoise = barrierNoise.getValue();
        if (Double.isNaN(cachedBarrierNoise))
        {
            // Avoids calculating the barrier noise for every invocation of calculatePressure, or if it's not needed at all
            final double barrierNoiseValue = this.barrierNoise.getValue(x, y * 0.5, z);
            barrierNoise.setValue(barrierNoiseValue);
            return barrierNoiseValue + pressure;
        }
        return cachedBarrierNoise + pressure;
    }

    private AquiferEntry getOrCreateAquifer(long location)
    {
        // Queries the cache first, and generates if the cache isn't found
        final int x = BlockPos.getX(location);
        final int y = BlockPos.getY(location);
        final int z = BlockPos.getZ(location);
        final int gridIndex = getIndex(gridXZ(x), gridY(y), gridXZ(z));

        AquiferEntry status = aquifers[gridIndex];
        if (status == null)
        {
            status = createAquifer(x, y, z);
            aquifers[gridIndex] = status;
        }
        return status;
    }

    private AquiferEntry createAquifer(int x, int y, int z)
    {
        final int dx = x - minChunkX + 16, dz = z - minChunkZ + 16; // In [0, 48)
        final int surfaceIndex = SectionPos.blockToSectionCoord(dx) + 4 * SectionPos.blockToSectionCoord(dz);
        final int surfaceHeight = surfaceHeights[surfaceIndex];

        if (y >= surfaceHeight)
        {
            // Above surface height, all aquifers must be sea level
            return seaLevelAquifer;
        }

        final Cellular3D.Cell cell = fluidCellNoise.cell(x, y / 0.6f, z);
        final float cellNoise = cell.noise();
        final float cellY = cell.y();

        if (cellNoise < 0.25f || (cellY > TFCChunkGenerator.SEA_LEVEL_Y - 10 && cellNoise < 0.5f))
        {
            return new AquiferEntry(Blocks.WATER.defaultBlockState(), minY - 1);
        }

        final RandomSource random = new XoroshiroRandomSource(fluidCellSeed, Float.floatToIntBits(cellNoise));
        final float aquiferY = Math.min((random.nextFloat() - random.nextFloat() - 2) * 5 + cellY, surfaceHeight);

        final boolean lava = cellY < 40 && (random.nextInt(3) == 0);
        return new AquiferEntry(lava ? Blocks.LAVA.defaultBlockState() : Blocks.WATER.defaultBlockState(), (int) aquiferY);
    }

    private AquiferEntry globalAquifer(int y)
    {
        return y < minY + 10 ? lavaLevelAquifer : seaLevelAquifer;
    }

    private int getIndex(int x, int y, int z)
    {
        final int dx = x - minGridX;
        final int dy = y - minGridY;
        final int dz = z - minGridZ;
        return (dy * gridSizeZ + dz) * gridSizeX + dx;
    }

    record AquiferEntry(BlockState state, int fluidY)
    {
        public BlockState at(int y)
        {
            return y < fluidY ? state : Blocks.AIR.defaultBlockState();
        }
    }
}
