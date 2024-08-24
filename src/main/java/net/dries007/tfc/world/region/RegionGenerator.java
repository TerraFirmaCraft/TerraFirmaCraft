/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;

import net.dries007.tfc.world.FastConcurrentCache;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.settings.Settings;

/**
 * This is a single-instance, threadsafe (accessible from multiple threads concurrently), generator. As such, all query-able fields of this class need to support concurrent access, either by being concurrent i.e. {@link FastConcurrentCache}, thread local {@link ThreadLocal}, or immutable / stateless i.e. {@link Noise2D}
 */
public class RegionGenerator
{
    private static double triangle(double frequency, double value)
    {
        return Math.abs(4f * frequency * value + 1f - 4f * Mth.floor(frequency * value + 0.75f)) - 1f;
    }

    private static Noise2D baseNoise(boolean axisIsX, float scale, float constant)
    {
        final float frequency = Units.GRID_WIDTH_IN_BLOCK / (2f * scale);
        return scale == 0 ?
            (x, z) -> constant : axisIsX ?
            (x, z) -> triangle(frequency, x) :
            (x, z) -> triangle(frequency, z);
    }

    public final Cellular2D cellNoise;
    public final Noise2D continentNoise;
    public final Noise2D temperatureNoise;
    public final Noise2D rainfallNoise;
    public final Noise2D rainfallVarianceNoise;

    public final ThreadLocal<Area> biomeArea;
    public final ThreadLocal<Area> rockArea;

    private final long seed;
    private final FastConcurrentCache<Region> cellCache;
    private final FastConcurrentCache<RegionPartition> partitionCache;

    public RegionGenerator(Settings settings, RandomSource random)
    {
        this.seed = random.nextLong();

        this.cellNoise = new Cellular2D(random.nextLong()).spread(1f / Units.CELL_WIDTH_IN_GRID);

        // Both of these caches are queried, and cached, on a cell-coordinate basis
        // Since cells are large (~12km), a small concurrent cache should be enough
        this.cellCache = new FastConcurrentCache<>(256);
        this.partitionCache = new FastConcurrentCache<>(256);

        float min = settings.continentalness() * 10f - 2.5f; // range [0, 1], default 0.5 -> 2.5 continentalness
        this.continentNoise = cellNoise.then(c -> 1 - c.f1() / (0.37f + c.f2()))
            .lazyProduct(new OpenSimplex2D(random.nextLong())
                .spread(0.24f)
                .scaled(min, 8.7f)
                .octaves(4));

        this.temperatureNoise = baseNoise(false, settings.temperatureScale(), settings.temperatureConstant())
            .scaled(-20f, 30f)
            .add(new OpenSimplex2D(random.nextInt())
                .octaves(2)
                .spread(0.15f)
                .scaled(-3f, 3f));

        this.rainfallNoise = baseNoise(true, settings.rainfallScale(), settings.rainfallConstant())
            .scaled(0f, 500f)
            .add(new OpenSimplex2D(random.nextInt())
                .octaves(2)
                .spread(0.15f)
                .scaled(-80f, 40f)); // Bias slightly negative, as we bias near-ocean areas to be positive rainfall, so this encourages deserts inland

        this.rainfallVarianceNoise = baseNoise(true, settings.rainfallVarianceScale(), settings.rainfallConstant())
            .scaled(-0.0f, 0.0f)
            .add(new OpenSimplex2D(random.nextInt())
                .octaves(2)
                .spread(0.3f)
                .scaled(-.512f, 0.512f));

        final AreaFactory biomeAreaFactory = TFCLayers.createUniformLayer(random, 2);
        final AreaFactory rockAreaFactory = TFCLayers.createUniformLayer(random, 3);

        biomeArea = ThreadLocal.withInitial(biomeAreaFactory);
        rockArea = ThreadLocal.withInitial(rockAreaFactory);
    }

    public long seed()
    {
        return seed;
    }

    public RegionPartition.Point getOrCreatePartitionPoint(int gridX, int gridZ)
    {
        return getOrCreatePartition(gridX, gridZ).get(gridX, gridZ);
    }

    private RegionPartition getOrCreatePartition(int gridX, int gridZ)
    {
        final int cellX = Units.gridToCell(gridX);
        final int cellZ = Units.gridToCell(gridZ);

        RegionPartition entry = partitionCache.getIfPresent(cellX, cellZ);
        if (entry == null)
        {
            entry = createPartition(cellX, cellZ);
            partitionCache.set(cellX, cellZ, entry);
        }
        return entry;
    }

    private RegionPartition createPartition(int cellX, int cellZ)
    {
        final List<Region> nearbyRegions = getAllRegionsIn3x3CellArea(cellX, cellZ);
        final RegionPartition partition = new RegionPartition(cellX, cellZ);

        for (Region region : nearbyRegions)
        {
            for (RiverEdge edge : region.rivers())
            {
                for (int partX = edge.minPartX; partX <= edge.maxPartX; partX++)
                {
                    for (int partZ = edge.minPartZ; partZ <= edge.maxPartZ; partZ++)
                    {
                        if (partition.isIn(partX, partZ))
                        {
                            partition.getFromPart(partX, partZ).rivers().add(edge);
                        }
                    }
                }
            }
        }
        return partition;
    }

    private List<Region> getAllRegionsIn3x3CellArea(int cellX, int cellZ)
    {
        final List<Region> regions = new ArrayList<>(9);
        for (int dx = -1; dx <= 1; dx++)
        {
            for (int dz = -1; dz <= 1; dz++)
            {
                final Cellular2D.Cell regionCell = sampleCell(Units.cellToGrid(cellX + dx), Units.cellToGrid(cellZ + dz));
                regions.add(getOrCreateRegion(regionCell));
            }
        }
        return regions;
    }

    public Region.Point getOrCreateRegionPoint(int gridX, int gridZ)
    {
        return getOrCreateRegion(gridX, gridZ).atOrThrow(gridX, gridZ);
    }

    @VisibleForTesting
    public Region getOrCreateRegion(int gridX, int gridZ)
    {
        return getOrCreateRegion(sampleCell(gridX, gridZ));
    }

    private Region getOrCreateRegion(Cellular2D.Cell cell)
    {
        final int cellX = Float.floatToIntBits((float) cell.x());
        final int cellZ = Float.floatToIntBits((float) cell.y());

        Region entry = cellCache.getIfPresent(cellX, cellZ);
        if (entry == null)
        {
            entry = createRegion(cell, (id, r) -> {});
            cellCache.set(cellX, cellZ, entry);
        }
        return entry;
    }

    private Region createRegion(Cellular2D.Cell regionCell, BiConsumer<Task, Region> viewer)
    {
        return new Context(viewer, regionCell, seed).runTasks().region;
    }

    public Cellular2D.Cell sampleCell(int gridX, int gridZ)
    {
        return cellNoise.cell(gridX, gridZ);
    }

    @TestOnly
    public void visualizeRegion(int gridX, int gridZ, BiConsumer<Task, Region> viewer)
    {
        createRegion(sampleCell(gridX, gridZ), viewer);
    }

    @VisibleForTesting
    public enum Task
    {
        INIT(Init.INSTANCE),
        ADD_CONTINENTS(AddContinents.INSTANCE),
        ANNOTATE_DISTANCE_TO_CELL_EDGE(AnnotateDistanceToCellEdge.INSTANCE),
        FLOOD_FILL_SMALL_OCEANS(FloodFillSmallOceans.INSTANCE),
        ADD_ISLANDS(AddIslands.INSTANCE),
        ANNOTATE_DISTANCE_TO_OCEAN(AnnotateDistanceToOcean.INSTANCE),
        ANNOTATE_BASE_LAND_HEIGHT(AnnotateBaseLandHeight.INSTANCE),
        ANNOTATE_DISTANCE_TO_WEST_COAST(AnnotateDistanceToWestCoast.INSTANCE),
        ADD_MOUNTAINS(AddMountains.INSTANCE),
        ANNOTATE_BIOME_ALTITUDE(AnnotateBiomeAltitude.INSTANCE),
        ANNOTATE_CLIMATE(AnnotateClimate.INSTANCE),
        ANNOTATE_RAINFALL(c -> {}),
        ANNOTATE_RAINFALL_VARIANCE(c -> {}),
        CHOOSE_BIOMES(ChooseBiomes.INSTANCE),
        CHOOSE_ROCKS(ChooseRocks.INSTANCE),
        ADD_RIVERS_AND_LAKES(AddRiversAndLakes.INSTANCE),
        ;

        private static final Task[] VALUES = values();

        private final RegionTask task;

        Task(RegionTask task)
        {
            this.task = task;
        }
    }

    public class Context
    {
        private final BiConsumer<Task, Region> viewer;

        public final Cellular2D.Cell regionCell;
        public final RandomSource random;

        public final Region region;

        Context(BiConsumer<Task, Region> viewer, Cellular2D.Cell regionCell, long seed)
        {
            this.viewer = viewer;
            this.regionCell = regionCell;
            this.region = new Region(regionCell);

            final long regionSeed = seed ^ Float.floatToIntBits((float) regionCell.noise()) * 7189234123L;
            this.random = new XoroshiroRandomSource(regionSeed);
        }

        Context runTasks()
        {
            for (Task task : Task.VALUES)
            {
                run(task);
            }
            return this;
        }

        void run(Task task)
        {
            task.task.apply(this);
            viewer.accept(task, region);
        }

        public RegionGenerator generator()
        {
            return RegionGenerator.this;
        }
    }
}
