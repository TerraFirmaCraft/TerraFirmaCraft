package net.dries007.tfc.world;

import java.util.Arrays;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * This is a mapped version of {@link net.minecraft.world.level.levelgen.Aquifer.NoiseBasedAquifer}
 * It has a few very small modifications made:
 * - It doesn't require a {@code NoiseSampler}
 * - Some of the constants have been unpicked, parameters mapped, commented, etc.
 * - It stores the chunk position it was sourced from.
 */
public class TFCAquifer implements AquiferExtension
{
    private static final int X_RANGE = 10;
    private static final int Y_RANGE = 9;
    private static final int Z_RANGE = 10;
    private static final int X_SPACING = 16;
    private static final int Y_SPACING = 12;
    private static final int Z_SPACING = 16;

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

    private final ChunkPos chunkPos;
    private final NormalNoise barrierNoise;
    private final NormalNoise waterLevelNoise;
    private final NormalNoise lavaNoise;
    private final NoiseGeneratorSettings noiseGeneratorSettings;
    private final AquiferStatus[] aquiferCache;
    private final long[] aquiferLocationCache;
    private final int minGridX;
    private final int minGridY;
    private final int minGridZ;
    private final int gridSizeX;
    private final int gridSizeZ;
    private boolean shouldScheduleFluidUpdate;

    public TFCAquifer(ChunkPos chunkPos, NormalNoise barrierNoise, NormalNoise waterLevelNoise, NormalNoise lavaNoise, NoiseGeneratorSettings noiseGeneratorSettings, int minY, int height)
    {
        this.chunkPos = chunkPos;
        this.barrierNoise = barrierNoise;
        this.waterLevelNoise = waterLevelNoise;
        this.lavaNoise = lavaNoise;
        this.noiseGeneratorSettings = noiseGeneratorSettings;
        this.minGridX = gridX(chunkPos.getMinBlockX()) - 1;
        int maxGridX = gridX(chunkPos.getMaxBlockX()) + 1;
        this.gridSizeX = maxGridX - minGridX + 1;
        this.minGridY = gridY(minY) - 1;
        int maxGridY = gridY(minY + height) + 1;
        int gridSizeY = maxGridY - minGridY + 1;
        this.minGridZ = gridZ(chunkPos.getMinBlockZ()) - 1;
        int maxGridZ = gridZ(chunkPos.getMaxBlockZ()) + 1;
        this.gridSizeZ = maxGridZ - minGridZ + 1;
        int gridSize = gridSizeX * gridSizeY * gridSizeZ;
        this.aquiferCache = new AquiferStatus[gridSize];
        this.aquiferLocationCache = new long[gridSize];
        Arrays.fill(aquiferLocationCache, Long.MAX_VALUE);
    }

    @Override
    public ChunkPos getPos()
    {
        return chunkPos;
    }

    @Override
    public BlockState computeState(BaseStoneSource stoneSource, int x, int y, int z, double terrainNoise)
    {
        if (terrainNoise <= 0.0D) // terrain is non-solid, so should normally place air or water
        {
            double resultNoiseValue;
            BlockState resultFluidOrAirState;
            boolean resultIsProbablyInterestingEnough;
            if (this.isLavaLevel(y)) // lowest 9 y values in the world
            {
                resultFluidOrAirState = Blocks.LAVA.defaultBlockState(); // always place lava
                resultNoiseValue = 0.0D; // don't bother carving any extra area or something
                resultIsProbablyInterestingEnough = false;
            }
            else
            {
                int gridX = Math.floorDiv(x - 5, X_SPACING);
                int gridY = Math.floorDiv(y + 1, Y_SPACING);
                int gridZ = Math.floorDiv(z - 5, Z_SPACING);
                int closestDistanceSq = Integer.MAX_VALUE;
                int secondClosestDistanceSq = Integer.MAX_VALUE;
                int thirdClosestDistanceSq = Integer.MAX_VALUE;
                long closestAquiferIndex = 0L;
                long secondClosestAquiferIndex = 0L;
                long thirdClosestAquiferIndex = 0L;

                for (int dx = 0; dx <= 1; ++dx)
                {
                    for (int dy = -1; dy <= 1; ++dy)
                    {
                        for (int dz = 0; dz <= 1; ++dz)
                        {
                            // iterate through the corners of the grid
                            int gridCornerX = gridX + dx;
                            int gridCornerY = gridY + dy;
                            int gridCornerZ = gridZ + dz;
                            int gridCornerIndex = getIndex(gridCornerX, gridCornerY, gridCornerZ);
                            long aquiferLocationCached = this.aquiferLocationCache[gridCornerIndex]; // query the aquifer status at the cache if it has already been generated
                            long aquiferLocation;
                            if (aquiferLocationCached != Long.MAX_VALUE)
                            {
                                // already generated the aquifer location for this grid corner?
                                aquiferLocation = aquiferLocationCached;
                            }
                            else
                            {
                                // need to generate an aquifer location for this corner
                                // seeds a random using the grid position
                                // The aquifer location is equal to the grid corner, in absolute block coordinates, plus a random offset which is not quite the width/size of the grid
                                // Thus, aquifer locations are stuck in the bottom left sort of region of the grid
                                WorldgenRandom random = new WorldgenRandom(Mth.getSeed(gridCornerX, gridCornerY * 3, gridCornerZ) + 1L);
                                aquiferLocation = BlockPos.asLong(
                                    gridCornerX * X_SPACING + random.nextInt(X_RANGE),
                                    gridCornerY * Y_SPACING + random.nextInt(Y_RANGE),
                                    gridCornerZ * Z_SPACING + random.nextInt(Z_RANGE)
                                );
                                this.aquiferLocationCache[gridCornerIndex] = aquiferLocation;
                            }

                            // Extract the coordinates out of the aquifer location, these are the distances
                            int distanceX = BlockPos.getX(aquiferLocation) - x;
                            int distanceY = BlockPos.getY(aquiferLocation) - y;
                            int distanceZ = BlockPos.getZ(aquiferLocation) - z;
                            // Total distance to this aquifer
                            int distanceSquared = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
                            if (closestDistanceSq >= distanceSquared)
                            {
                                // a new smallest distance
                                // bump all the distances down by one
                                thirdClosestAquiferIndex = secondClosestAquiferIndex;
                                secondClosestAquiferIndex = closestAquiferIndex;
                                closestAquiferIndex = aquiferLocation;

                                thirdClosestDistanceSq = secondClosestDistanceSq;
                                secondClosestDistanceSq = closestDistanceSq;
                                closestDistanceSq = distanceSquared;
                            }
                            else if (secondClosestDistanceSq >= distanceSquared)
                            {
                                // only bump them down by the second two
                                thirdClosestAquiferIndex = secondClosestAquiferIndex;
                                secondClosestAquiferIndex = aquiferLocation;

                                thirdClosestDistanceSq = secondClosestDistanceSq;
                                secondClosestDistanceSq = distanceSquared;
                            }
                            else if (thirdClosestDistanceSq >= distanceSquared)
                            {
                                // only replace the third
                                thirdClosestAquiferIndex = aquiferLocation;
                                thirdClosestDistanceSq = distanceSquared;
                            }
                        }
                    }
                }

                // At this point, we have:
                // (x, y, z) -> the real world block coordinates we're trying to check
                // gridX/Y/Z -> the lower corner grid cell that these block coordinates belong to
                // the three closest aquifers - one is placed at each grid location (jittered a bit), and we sample the nearby corners
                // of the grid cell the (x, y, z) belongs to, and then find and order the three that are closest by euclidean distance squared
                // we have then, the 'location' (as a long, which represents an index into a cache), and the distance (dist sq)

                // Get the aquifers at each location
                // These have two pieces of data: a block state (basically either lava or water), and a y-level
                AquiferStatus closestAquifer = this.getAquiferStatus(closestAquiferIndex);
                AquiferStatus secondClosestAquifer = this.getAquiferStatus(secondClosestAquiferIndex);
                AquiferStatus thirdClosestAquifer = this.getAquiferStatus(thirdClosestAquiferIndex);

                // Find the similarity between each aquifer
                // we have three, so this forms a triangle
                // Values close to 1 = the two aquifers are similar in distance, values < 1 indicate they are very different
                double closestToSecondSimilarity = this.similarity(closestDistanceSq, secondClosestDistanceSq);
                double closestToThirdSimilarity = this.similarity(closestDistanceSq, thirdClosestDistanceSq);
                double secondToThirdSimilarity = this.similarity(secondClosestDistanceSq, thirdClosestDistanceSq);

                // We have an interesting result if the first and second aquifers are not too close together
                resultIsProbablyInterestingEnough = closestToSecondSimilarity > 0.0D;
                if (closestAquifer.fluidLevel >= y && closestAquifer.fluidType.is(Blocks.WATER) && this.isLavaLevel(y - 1))
                {
                    // IF, our closest aquifer is a water one, and the current y value below is is lava level, and we're beneath the aquifer...
                    // well, then we kind of run into a problem. Lava and water don't mix y'all
                    // idk what this does yet...
                    resultNoiseValue = 1.0D;
                }
                else if (closestToSecondSimilarity > -1.0D)
                {
                    // Otherwise, we look at the closest to second similarity. If it's > -1.0, we probably have something mildly interesting
                    // Again, this means the closest and second closest aquifers are not the same distance away from the current position - one is closer

                    double barrierNoiseValue = 1.0D + (this.barrierNoise.getValue(x, y, z) + 0.05D) / 4.0D;

                    // calculate 'pressure' values for the aquifers... yeah idk what that means either
                    // This returns 1.0 in the case where we're below both aquifers y level, and they are different fluids
                    // in all other cases, this returns another weird calculation involving y levels and stuff.
                    double closestToSecondPressure = this.calculatePressure(y, barrierNoiseValue, closestAquifer, secondClosestAquifer);
                    double closestToThirdPressure = this.calculatePressure(y, barrierNoiseValue, closestAquifer, thirdClosestAquifer);
                    double secondToThirdPressure = this.calculatePressure(y, barrierNoiseValue, secondClosestAquifer, thirdClosestAquifer);

                    // clamp the similarity (how close each distance is to the other distance) to be between 1 and 0. 1 = exactly the same distance apart (so our x/y/z position is probably equally spaced between them!!) and 0 = we're probably solidly on one or the other side
                    double clampedClosestToSecondSimilarity = Math.max(0.0D, closestToSecondSimilarity);
                    double clampedClosestToThirdSimilarity = Math.max(0.0D, closestToThirdSimilarity);
                    double clampedSecondToThirdSimilarity = Math.max(0.0D, secondToThirdSimilarity);

                    // 1-2similarity * max(1-2pressure, max(1-3pressure*1-3similarity, 2-3pressure*2-3similarity))
                    // so... what this does...
                    // if the first and second closest aquifers are equidistant apart, 1-2similarity is zero. the noise tends towards zero
                    // so in order for us to have a > 0 noise value, we need to be solidly on one side of the two closest aquifers (which makes sense, thinking about > 0 noise = stone, 0 = fluid)
                    // Then, we can take the largest of two values - we need to either be solidly on one side of 1-3, or 2-3. AKA, since we're already solidly on one side of 1-2, whichever one we're on one side of, we need to be solidly on one side of <that one>-3
                    // Basically, given the three closest aquifers, we cannot be in the 'boundary' region of any two of them.
                    // ... somewhere the pressure comes into it, I don't quite understand where
                    double newNoise = 2.0D * clampedClosestToSecondSimilarity * Math.max(closestToSecondPressure, Math.max(closestToThirdPressure * clampedClosestToThirdSimilarity, secondToThirdPressure * clampedSecondToThirdSimilarity));

                    // And then make sure it's positive
                    resultNoiseValue = Math.max(0.0D, newNoise);
                }
                else
                {
                    // where is this else{} block from? oh yeah, if the first and second aquifers were SO SIMILAR in distance, we just had to put them together
                    // there's no hope for them, we're on that boundary, so we're placing stone there
                    resultNoiseValue = 0.0D;
                }

                // the closest aquifer can tell us the fluid state (if we're beneath it's y level), IF we aren't on a boundary region.
                resultFluidOrAirState = y >= closestAquifer.fluidLevel ? Blocks.AIR.defaultBlockState() : closestAquifer.fluidType;
            }

            // The big kicker... remember when I was guessing that high noise values = stone and low ones = fluid (or air?)
            // well turns out that was right. Terrain noise is already negative (since we're in an air zone), and so this gives it a 'boost' near aquifer borders
            // if the boost is enough, then we skip this if statement, and run the bit below, which places stone
            if (terrainNoise + resultNoiseValue <= 0.0D)
            {
                // otherwise, if resultNoiseValue is small enough, then we arrive here
                // we schedule a fluid update based on the heuristic that we did above for interesting enough (weird)
                // and we return the precomputed fluid or air state. badda bing, badda boom, we have an aquifer
                this.shouldScheduleFluidUpdate = resultIsProbablyInterestingEnough;
                return resultFluidOrAirState;
            }
        }

        // idk this part is boring. we just return stone
        this.shouldScheduleFluidUpdate = false;
        return stoneSource.getBaseBlock(x, y, z);
    }

    public boolean shouldScheduleFluidUpdate()
    {
        return this.shouldScheduleFluidUpdate;
    }

    /**
     * @return The index into caches for this aquifer, based on the grid size / scale
     */
    private int getIndex(int x, int y, int z)
    {
        final int localX = x - this.minGridX;
        final int localY = y - this.minGridY;
        final int localZ = z - this.minGridZ;
        return (localY * this.gridSizeZ + localZ) * this.gridSizeX + localX;
    }

    private boolean isLavaLevel(int y)
    {
        return y - this.noiseGeneratorSettings.noiseSettings().minY() <= ALWAYS_LAVA_AT_OR_BELOW_Y_INDEX;
    }

    /**
     * When the distances are very similar, returns values close to 1
     * When the distances are very different, returns values < 1
     * This function, I would assume, is normalized (with the /25), such that based on the squared distances passed in, only returns values in [0, 1]
     */
    private double similarity(int firstDistance, int secondDistance)
    {
        return 1.0D - Math.abs(secondDistance - firstDistance) / 25.0D;
    }

    private double calculatePressure(int y, double barrierNoiseValue, AquiferStatus firstAquifer, AquiferStatus secondAquifer)
    {
        if (y <= firstAquifer.fluidLevel && y <= secondAquifer.fluidLevel && firstAquifer.fluidType != secondAquifer.fluidType)
        {
            // If we are beneath both aquifers, and the fluid types are NOT THE SAME... then they can't mix.
            // We return 1.0 here... probably to indicate we place stone here I think
            return 1.0D;
        }
        else
        {
            // How different are the aquifers in height?
            int differenceInAquiferFluidHeight = Math.abs(firstAquifer.fluidLevel - secondAquifer.fluidLevel);
            // The average of their two fluid heights
            double averageAquiferFluidHeight = 0.5D * (double) (firstAquifer.fluidLevel + secondAquifer.fluidLevel);
            // Now, the distance between the y value, and the average height
            double distanceToAverageAquiferFluidHeight = Math.abs(averageAquiferFluidHeight - y - 0.5D);

            // This return value...
            // scaled by the total difference between the two aquifers...
            /// but, reduced the further we are away from the midpoint of the two aquifers
            return 0.5D * (double) differenceInAquiferFluidHeight * barrierNoiseValue - distanceToAverageAquiferFluidHeight;
        }
    }

    private AquiferStatus getAquiferStatus(long aquiferPos)
    {
        // all we have is the aquifer position
        // we can extract the x/y/z coordinates from the long
        int x = BlockPos.getX(aquiferPos);
        int y = BlockPos.getY(aquiferPos);
        int z = BlockPos.getZ(aquiferPos);
        // And then use those to find the grid x/y/z that this aquifer comes from (since all aquifers are jittered a bit positively from the grid coordinate
        int gridX = gridX(x);
        int gridY = gridY(y);
        int gridZ = gridZ(z);
        // And from the grid coordinates compute an index
        int gridIndex = this.getIndex(gridX, gridY, gridZ);
        // which we can then check the cache for!
        AquiferStatus aquiferStatus = this.aquiferCache[gridIndex];
        if (aquiferStatus != null)
        {
            // cached aquifer
            return aquiferStatus;
        }
        else
        {
            // Cache miss, so we actually have to compute an aquifer here
            // All we know is that there is an aquifer at (x, y, z). We can get the grid coordinates from that
            AquiferStatus newAquiferStatus = this.computeAquifer(x, y, z);
            this.aquiferCache[gridIndex] = newAquiferStatus;
            return newAquiferStatus;
        }
    }

    private AquiferStatus computeAquifer(int x, int y, int z)
    {
        int seaLevel = this.noiseGeneratorSettings.seaLevel();
        if (y > ALWAYS_USE_SEA_LEVEL_WHEN_ABOVE)
        {
            // Above y=30, all aquifers are at sea level, and consist of water
            return new AquiferStatus(seaLevel, Blocks.WATER.defaultBlockState());
        }
        else
        {
            // below y=30, the water level may vary depending on a noise value... which we now query
            // it's a 3d noise, and as you can see, the value passed in, because of the floor div, is going to be the same across larger areas
            double waterLevelNoiseValue = this.waterLevelNoise.getValue(Math.floorDiv(x, 64), Math.floorDiv(y, 40) / 1.4D, Math.floorDiv(z, 64)) * 30.0D + -10.0D;
            // if the aquifer is a lava one
            boolean atLavaLevel = false;

            // some fancy noise transformations
            if (Math.abs(waterLevelNoiseValue) > 8.0D)
            {
                waterLevelNoiseValue *= 4.0D;
            }

            // Note, if -40 < y < 0, then this will be -20.
            int veryRoundedYValue = Math.floorDiv(y, 40) * 40 + 20;
            int aquiferYLocation = veryRoundedYValue + Mth.floor(waterLevelNoiseValue);
            if (veryRoundedYValue == -20) // lava aquifers only occur around the y-level of -20 (adjusted by noise)
            {
                // sample the lava noise at the same location
                double lavaNoiseValue = this.lavaNoise.getValue(Math.floorDiv(x, 64), Math.floorDiv(y, 40) / 1.4D, Math.floorDiv(z, 64));
                atLavaLevel = Math.abs(lavaNoiseValue) > (double) 0.22F; // just check that in absolute value, there is enough to make this a lava aquifer
            }

            // The aquifer CANNOT be higher than 56. (presumably, because all aquifers above that normalize to sea level)
            // lava aquifers will appear *around* -20, although water aquifers can appear above and below that?
            return new AquiferStatus(Math.min(56, aquiferYLocation), atLavaLevel ? Blocks.LAVA.defaultBlockState() : Blocks.WATER.defaultBlockState());
        }
    }

    record AquiferStatus(int fluidLevel, BlockState fluidType) {}
}
