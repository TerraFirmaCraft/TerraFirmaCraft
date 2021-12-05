package net.dries007.tfc.world;

import java.util.Arrays;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableDouble;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;

public class NoiseBasedAquifer
{
    private static final int X_RANGE = 10;
    private static final int Y_RANGE = 9;
    private static final int Z_RANGE = 10;

    private static final int X_SPACING = 16;
    private static final int Y_SPACING = 12;
    private static final int Z_SPACING = 16;

    private static final double FLOWING_UPDATE_SIMILARITY = similarity(Mth.square(10), Mth.square(12));

    private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][] {
                 {-2, -1}, {-1, -1}, {0, -1}, {1, -1},
        {-3, 0}, {-2, 0},  {-1, 0},  {0, 0},  {1, 0},
                 {-2, 1},  {-1, 1},  {0, 1},  {1, 1}
    };

    /**
     * When the distances are very similar, returns values close to 1
     * When the distances are very different, returns values < 1
     */
    private static double similarity(int firstDistance, int secondDistance)
    {
        return 1 - Math.abs(secondDistance - firstDistance) / 25d;
    }

    private static int gridX(int x)
    {
        return Math.floorDiv(x, X_SPACING);
    }

    private static int gridY(int y)
    {
        return Math.floorDiv(y, Y_SPACING);
    }

    private static int gridZ(int z)
    {
        return Math.floorDiv(z, Z_SPACING);
    }


    private final NormalNoise barrierNoise;
    private final NormalNoise fluidLevelFloodednessNoise;
    private final NormalNoise fluidLevelSpreadNoise;
    private final NormalNoise lavaNoise;
    private final PositionalRandomFactory positionalRandomFactory;

    private final FluidStatus[] aquiferCache;
    private final long[] aquiferLocationCache;

    private final int minY;
    private final int minGridX, minGridY, minGridZ;
    private final int gridSizeX, gridSizeZ;

    private final FluidStatus lavaLevelStatus;
    private final FluidStatus seaLevelStatus;

    private boolean shouldScheduleFluidUpdate;

    public NoiseBasedAquifer(ChunkNoiseSamplingSettings settings, ChunkPos chunkPos, NormalNoise barrierNoise, NormalNoise fluidLevelFloodednessNoise, NormalNoise fluidLevelSpreadNoise, NormalNoise lavaNoise, PositionalRandomFactory positionalRandomFactory, int seaLevel)
    {

        this.barrierNoise = barrierNoise;
        this.fluidLevelFloodednessNoise = fluidLevelFloodednessNoise;
        this.fluidLevelSpreadNoise = fluidLevelSpreadNoise;
        this.lavaNoise = lavaNoise;
        this.positionalRandomFactory = positionalRandomFactory;

        final int maxGridX = gridX(chunkPos.getMaxBlockX()) + 1;
        final int maxGridZ = gridZ(chunkPos.getMaxBlockZ()) + 1;
        final int maxGridY = gridY(settings.firstCellY() + settings.cellCountY()) + 1;

        this.minGridX = gridX(chunkPos.getMinBlockX()) - 1;
        this.minGridY = gridY(settings.firstCellY()) - 1;
        this.minGridZ = gridZ(chunkPos.getMinBlockZ()) - 1;

        this.gridSizeX = maxGridX - minGridX + 1;
        int gridSizeY = maxGridY - minGridY + 1;
        this.gridSizeZ = maxGridZ - minGridZ + 1;

        // Initialize caches
        final int cacheSize = gridSizeX * gridSizeY * gridSizeZ;

        this.aquiferCache = new FluidStatus[cacheSize];
        this.aquiferLocationCache = new long[cacheSize];

        Arrays.fill(this.aquiferLocationCache, Long.MAX_VALUE);

        this.minY = settings.minY();
        this.lavaLevelStatus = new FluidStatus(minY + 10, Blocks.LAVA.defaultBlockState());
        this.seaLevelStatus = new FluidStatus(seaLevel, Blocks.WATER.defaultBlockState());
    }

    @Nullable
    public BlockState computeSubstance(int x, int y, int z, double baseNoise, double modifiedNoise)
    {
        // Noise values < 0 indicate air, > 0 indicate solid blocks.
        if (baseNoise <= -64)
        {
            // < -64 implies we must be way above the surface, so we just use the global aquifer.
            return getGlobalFluidStatus(y).at(y);
        }
        else if (modifiedNoise <= 0)
        {
            // < 0 would generate air block (and we checked the modified noise, including contributions from caves)
            final FluidStatus globalStatus = getGlobalFluidStatus(y);

            double aquiferNoiseContribution; // The contribution from aquifer borders to the noise
            BlockState state; // The result aquifer state
            boolean shouldScheduleUpdate; // If the similarity is between a threshold heuristic we schedule an update

            if (globalStatus.at(y).is(Blocks.LAVA))
            {
                // Always lava below lava level, and don't generate adjacent borders.
                state = Blocks.LAVA.defaultBlockState();
                aquiferNoiseContribution = 0;
                shouldScheduleUpdate = false;
            }
            else
            {
                final int centerGridX = Math.floorDiv(x - 5, X_SPACING);
                final int centerGridY = Math.floorDiv(y + 1, Y_SPACING);
                final int centerGridZ = Math.floorDiv(z - 5, Z_SPACING);

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
                            final int adjGridX = centerGridX + offsetGridX;
                            final int adjGridY = centerGridY + offsetGridY;
                            final int adjGridZ = centerGridZ + offsetGridZ;
                            final int adjIndex = getIndex(adjGridX, adjGridY, adjGridZ);

                            long adjAquifer = aquiferLocationCache[adjIndex];
                            if (adjAquifer == Long.MAX_VALUE)
                            {
                                // Compute and cache the aquifer location at this index
                                final RandomSource random = positionalRandomFactory.at(adjGridX, adjGridY, adjGridZ);
                                adjAquifer = BlockPos.asLong(
                                    adjGridX * X_SPACING + random.nextInt(X_RANGE),
                                    adjGridY * Y_SPACING + random.nextInt(Y_RANGE),
                                    adjGridZ * Z_SPACING + random.nextInt(Z_RANGE));
                                aquiferLocationCache[adjIndex] = adjAquifer;
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
                final FluidStatus status1 = getAquiferStatus(aquifer1);
                final FluidStatus status2 = getAquiferStatus(aquifer2);
                final FluidStatus status3 = getAquiferStatus(aquifer3);

                // Similarity between each pair of aquifers
                double similarity12 = similarity(distance1, distance2);
                double similarity13 = similarity(distance1, distance3);
                double similarity23 = similarity(distance2, distance3);

                if (status1.at(y).is(Blocks.WATER) && getGlobalFluidStatus(y - 1).at(y - 1).is(Blocks.LAVA))
                {
                    // Border lava and water with solid blocks.
                    aquiferNoiseContribution = 1;
                }
                else if (similarity12 > -1)
                {
                    final MutableDouble barrierNoise = new MutableDouble(Double.NaN);

                    // Pressure between each pair of aquifers
                    final double pressure12 = calculatePressure(x, y, z, barrierNoise, status1, status2);
                    final double pressure13 = calculatePressure(x, y, z, barrierNoise, status1, status3);
                    final double pressure23 = calculatePressure(x, y, z, barrierNoise, status2, status3);

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

                state = status1.at(y);
                shouldScheduleUpdate = similarity12 >= FLOWING_UPDATE_SIMILARITY; // Heuristic if we should schedule a fluid update
            }

            if (modifiedNoise + aquiferNoiseContribution <= 0)
            {
                this.shouldScheduleFluidUpdate = shouldScheduleUpdate;
                return state;
            }
        }
        this.shouldScheduleFluidUpdate = false;
        return null;
    }

    public boolean shouldScheduleFluidUpdate()
    {
        return this.shouldScheduleFluidUpdate;
    }

    private double calculatePressure(int x, int y, int z, MutableDouble barrierNoise, FluidStatus leftStatus, FluidStatus rightStatus)
    {
        final BlockState leftState = leftStatus.at(y);
        final BlockState rightState = rightStatus.at(y);

        if ((leftState.is(Blocks.LAVA) && rightState.is(Blocks.WATER)) || (leftState.is(Blocks.WATER) && rightState.is(Blocks.LAVA)))
        {
            // Pressure between different fluid types aquifers is always one (maximum).
            return 1;
        }

        final int deltaFluidLevel = Math.abs(leftStatus.fluidLevel - rightStatus.fluidLevel);
        if (deltaFluidLevel == 0)
        {
            // Pressure between aquifers at the same fluid level (height) is zero.
            return 0;
        }

        final double averageFluidLevel = 0.5 * (leftStatus.fluidLevel + rightStatus.fluidLevel);
        final double deltaAboveAverageFluidLevel = y + 0.5 - averageFluidLevel;
        double deltaNearAverageFluidLevel = (0.5 * deltaFluidLevel) - Math.abs(deltaAboveAverageFluidLevel);
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

    private FluidStatus getAquiferStatus(long location)
    {
        // Queries the cache first, and generates if the cache isn't found
        final int x = BlockPos.getX(location);
        final int y = BlockPos.getY(location);
        final int z = BlockPos.getZ(location);
        final int gridIndex = getIndex(gridX(x), gridY(y), gridZ(z));

        FluidStatus status = aquiferCache[gridIndex];
        if (status == null)
        {
            status = computeFluid(x, y, z);
            aquiferCache[gridIndex] = status;
        }
        return status;
    }

    private FluidStatus computeFluid(int x, int y, int z)
    {
        final FluidStatus globalStatus = getGlobalFluidStatus(y);

        int minNearbySurfaceLevel = Integer.MAX_VALUE;

        final int maxYRange = y + 12;
        final int minYRange = y - 12;

        boolean underApproxSeaLevel = false;

        for (int[] offsetXZ : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS)
        {
            final int offsetX = x + SectionPos.sectionToBlockCoord(offsetXZ[0]);
            final int offsetZ = z + SectionPos.sectionToBlockCoord(offsetXZ[1]);

            // This is an estimation of the surface level of the nearby area
            final int surfaceLevelAtOffset = 70;//this.noiseChunk.preliminarySurfaceLevel(offsetX, offsetZ); // todo: surface level?
            final int maxSurfaceLevelAtOffset = surfaceLevelAtOffset + 8;

            final boolean atTargetChunk = offsetXZ[0] == 0 && offsetXZ[1] == 0; // When the offsets are zero
            if (atTargetChunk && minYRange > maxSurfaceLevelAtOffset)
            {
                // The y position is high enough above the preliminary surface at the target chunk that we exit here
                return globalStatus;
            }

            boolean flag2 = maxYRange > maxSurfaceLevelAtOffset;
            if (flag2 || atTargetChunk)
            {
                final FluidStatus offsetStatus = getGlobalFluidStatus(maxSurfaceLevelAtOffset);
                if (!offsetStatus.at(maxSurfaceLevelAtOffset).isAir())
                {
                    if (atTargetChunk)
                    {
                        underApproxSeaLevel = true;
                    }

                    if (flag2)
                    {
                        return offsetStatus;
                    }
                }
            }

            minNearbySurfaceLevel = Math.min(minNearbySurfaceLevel, surfaceLevelAtOffset);
        }

        int deltaYToNearbySurface = minNearbySurfaceLevel + 8 - y;
        double d1 = underApproxSeaLevel ? Mth.clampedMap(deltaYToNearbySurface, 0, 64, 1, 0) : 0;
        double floodedness = Mth.clamp(fluidLevelFloodednessNoise.getValue(x, y * 0.67, z), -1, 1);
        double d4 = Mth.map(d1, 1, 0, -0.3, 0.8);
        if (floodedness > d4)
        {
            return globalStatus;
        }
        else
        {
            double d5 = Mth.map(d1, 1, 0, -0.8, 0.4);
            if (floodedness <= d5)
            {
                return new FluidStatus(DimensionType.WAY_BELOW_MIN_Y, globalStatus.fluidType);
            }
            else
            {
                final int largeGridX = Math.floorDiv(x, 16);
                final int largeGridY = Math.floorDiv(y, 40);
                final int largeGridZ = Math.floorDiv(z, 16);

                final int centerY = largeGridY * 40 + 20; // The center y level of a large grid cell
                final double fluidLevelSpreadValue = this.fluidLevelSpreadNoise.getValue(largeGridX, largeGridY / 1.4, largeGridZ) * 10;
                final int quantizedFluidLevelSpread = Mth.quantize(fluidLevelSpreadValue, 3);
                final int centerYAndVariance = centerY + quantizedFluidLevelSpread;
                return new FluidStatus(Math.min(minNearbySurfaceLevel, centerYAndVariance), getFluidType(x, y, z, globalStatus, centerYAndVariance));
            }
        }
    }

    private BlockState getFluidType(int x, int y, int z, FluidStatus globalStatus, int p_188437_)
    {
        if (p_188437_ <= -10)
        {
            final int largeCellX = Math.floorDiv(x, 64);
            final int largeCellY = Math.floorDiv(y, 40);
            final int largeCellZ = Math.floorDiv(z, 64);

            double lavaNoiseValue = lavaNoise.getValue(largeCellX, largeCellY, largeCellZ);
            if (Math.abs(lavaNoiseValue) > 0.3)
            {
                return Blocks.LAVA.defaultBlockState();
            }
        }
        return globalStatus.fluidType;
    }

    private FluidStatus getGlobalFluidStatus(int y)
    {
        return y < minY + 10 ? lavaLevelStatus : seaLevelStatus;
    }

    private int getIndex(int x, int y, int z)
    {
        final int dx = x - minGridX;
        final int dy = y - minGridY;
        final int dz = z - minGridZ;
        return (dy * gridSizeZ + dz) * gridSizeX + dx;
    }

    record FluidStatus(int fluidLevel, BlockState fluidType)
    {
        public BlockState at(int y)
        {
            return y < fluidLevel ? fluidType : Blocks.AIR.defaultBlockState();
        }
    }
}
