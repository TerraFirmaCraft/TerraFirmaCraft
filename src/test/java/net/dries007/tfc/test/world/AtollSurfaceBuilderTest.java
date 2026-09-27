/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.world;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.test.TestSetup;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.LerpFloatLayer;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.builder.AtollSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SurfaceBuilder;
import net.dries007.tfc.world.volcano.CenteredFeatureNoise;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Drives {@link AtollSurfaceBuilder#buildSurface} with real chunks. Noise positions are fixed by the reported world seed.
 */
public class AtollSurfaceBuilderTest implements TestSetup
{
    /** Seed from <a href="https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/3719">issue 3719</a>. */
    private static final long LEVEL_SEED = 100512285190304202L;
    private static final int MIN_Y = 0;
    private static final int HEIGHT = 256;
    private static final int SEA_LEVEL = TFCChunkGenerator.SEA_LEVEL_Y;
    /** Highest solid still leaves salt water at sea level - 1 and sea level - 2. */
    private static final int RAISED_FLOOR = SEA_LEVEL - 3;
    private static final int DEEP_FLOOR = 20;

    private static Catalog catalog;
    private static Sample raisedEnclosed;
    private static Sample raisedOpen;
    private static RockLayerSettings rockLayers;
    private static Registry<Biome> biomes;

    @Test
    public void testDeepFloorStillBecomesFresh()
    {
        final Sample sample = raisedEnclosed();
        final ProtoChunk chunk = lagoon(sample, DEEP_FLOOR, saltWater());
        final int oceanFloor = requireOceanFloor(chunk, sample, DEEP_FLOOR);
        final int preVolcanicHeight = SEA_LEVEL - 40;
        final int maxDepth = Math.max(preVolcanicHeight, sample.volcanoHeight());
        assertTrue(oceanFloor < maxDepth + 2, () -> "deep floor should miss the terrain gate: " + sample + " ocean=" + oceanFloor);

        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN_ATOLLS, preVolcanicHeight, true);

        assertEquals(freshWater(), at(chunk, sample, SEA_LEVEL - 1), "enclosed lagoon kept salt water below the floor gate");
        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 2));
        assertEquals(saltWater(), at(chunk, sample, DEEP_FLOOR + 1));
        assertEquals(raw(Rock.GRANITE), at(chunk, sample, DEEP_FLOOR));
        assertEquals(Blocks.AIR.defaultBlockState(), at(chunk, sample, SEA_LEVEL));
        assertEquals(1, parent.calls());
    }

    @Test
    public void testFloorGateEqualityStillBecomesFresh()
    {
        final Sample sample = raisedEnclosed();
        final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, saltWater());
        final int oceanFloor = requireOceanFloor(chunk, sample, RAISED_FLOOR);
        final int preVolcanicHeight = oceanFloor - 2;
        assertTrue(preVolcanicHeight >= sample.volcanoHeight(), () -> "equality not reachable: " + sample + " ocean=" + oceanFloor);
        assertEquals(oceanFloor, Math.max(preVolcanicHeight, sample.volcanoHeight()) + 2);

        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN_ATOLLS, preVolcanicHeight, true);

        assertEquals(freshWater(), at(chunk, sample, SEA_LEVEL - 1), "enclosed lagoon kept salt water on the floor gate");
        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 2));
        assertEquals(raw(Rock.GRANITE), at(chunk, sample, RAISED_FLOOR), "equality is a fallback column and should not replace terrain");
        assertEquals(1, parent.calls());
    }

    @Test
    public void testRaisedFloorKeepsFreshWaterAndSurface()
    {
        final Sample sample = raisedEnclosed();
        final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, saltWater());
        final int oceanFloor = requireOceanFloor(chunk, sample, RAISED_FLOOR);
        final int preVolcanicHeight = -64;
        final int maxDepth = Math.max(preVolcanicHeight, sample.volcanoHeight());
        assertTrue(oceanFloor > maxDepth + 2, () -> "raised floor should pass the terrain gate: " + sample + " ocean=" + oceanFloor);

        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN_ATOLLS, preVolcanicHeight, true);

        assertEquals(freshWater(), at(chunk, sample, SEA_LEVEL - 1));
        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 2));
        assertEquals(saltWater(), at(chunk, sample, RAISED_FLOOR + 1));
        assertEquals(expectedSand(sample.cell()), at(chunk, sample, RAISED_FLOOR));
        final int rockY = Math.min(RAISED_FLOOR - 1, Mth.floor(SEA_LEVEL - 11 + sample.randomHeight()));
        if (rockY >= maxDepth)
        {
            assertEquals(expectedRock(sample.cell()), at(chunk, sample, rockY));
        }
        assertEquals(raw(Rock.GRANITE), at(chunk, sample, maxDepth - 1), "terrain replacement stopped at maxDepth");
        assertEquals(0, parent.calls());
    }

    @Test
    public void testOpenAtollDoesNotFreshenWater()
    {
        final Sample sample = raisedOpen();
        assertTrue(sample.integrity() < 1);
        assertTrue(sample.easing() > 0.58);
        final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, saltWater());
        final int oceanFloor = requireOceanFloor(chunk, sample, RAISED_FLOOR);
        assertTrue(oceanFloor > sample.volcanoHeight() + 2, () -> sample.toString());

        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN_ATOLLS, -64, false);

        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 1));
        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 2));
        assertEquals(expectedSand(sample.cell()), at(chunk, sample, RAISED_FLOOR));
        assertEquals(0, parent.calls());
    }

    @Test
    public void testOutsideLagoonThresholdDoesNotFreshenWater()
    {
        final Sample sample = catalog().outside();
        assertTrue(sample.easing() > 0 && sample.easing() <= 0.58, sample::toString);
        final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, saltWater());
        final int oceanFloor = requireOceanFloor(chunk, sample, RAISED_FLOOR);
        assertTrue(oceanFloor > Math.max(-64, sample.volcanoHeight()) + 2, sample::toString);

        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN_ATOLLS, -64, false);

        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 1));
        assertEquals(expectedSand(sample.cell()), at(chunk, sample, RAISED_FLOOR));
        assertEquals(0, parent.calls());
    }

    @Test
    public void testFailedRarityDoesNotFreshenWater()
    {
        final Sample sample = catalog().rare();
        assertEquals(0f, sample.easing());
        assertTrue(Math.abs(sample.cell().noise()) > TFCBiomes.OCEAN_ATOLLS.getCenteredFeatureFrequency());
        final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, saltWater());

        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN_ATOLLS, -64, false);

        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 1));
        assertEquals(raw(Rock.GRANITE), at(chunk, sample, RAISED_FLOOR));
        assertEquals(1, parent.calls());
    }

    @Test
    public void testNonAtollCenterDoesNotFreshenWater()
    {
        final Sample sample = raisedEnclosed();
        assertFalse(TFCBiomes.OCEAN.hasAtolls());
        final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, saltWater());

        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN, -64, false);

        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 1));
        assertEquals(raw(Rock.GRANITE), at(chunk, sample, RAISED_FLOOR));
        assertEquals(1, parent.calls());
    }

    @Test
    public void testAirStoneAndFreshWaterStayUnchanged()
    {
        final Sample sample = raisedEnclosed();
        assertUnchangedSurface(sample, Blocks.AIR.defaultBlockState(), RAISED_FLOOR);
        assertUnchangedSurface(sample, Blocks.STONE.defaultBlockState(), SEA_LEVEL - 1);
        assertUnchangedSurface(sample, freshWater(), RAISED_FLOOR);
    }

    private static void assertUnchangedSurface(Sample sample, BlockState surface, int highestSolid)
    {
        final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, surface);
        final int oceanFloor = requireOceanFloor(chunk, sample, highestSolid);
        assertTrue(oceanFloor > sample.volcanoHeight() + 2, () -> surface + " " + sample + " ocean=" + oceanFloor);
        final RecordingParent parent = apply(chunk, sample, TFCBiomes.OCEAN_ATOLLS, -64, false);

        assertEquals(surface, at(chunk, sample, SEA_LEVEL - 1));
        assertEquals(saltWater(), at(chunk, sample, SEA_LEVEL - 2));
        assertEquals(expectedSand(sample.cell()), at(chunk, sample, RAISED_FLOOR));
        assertEquals(0, parent.calls());
    }

    private static ProtoChunk lagoon(Sample sample, int floorY, BlockState surface)
    {
        final ProtoChunk chunk = new ProtoChunk(new ChunkPos(new BlockPos(sample.x(), 0, sample.z())), UpgradeData.EMPTY, LevelHeightAccessor.create(MIN_Y, HEIGHT), biomes(), null);
        final BlockState rock = raw(Rock.GRANITE);
        final BlockState salt = saltWater();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = MIN_Y; y <= SEA_LEVEL + 8; y++)
        {
            final BlockState state = y <= floorY ? rock : y < SEA_LEVEL - 1 ? salt : y == SEA_LEVEL - 1 ? surface : Blocks.AIR.defaultBlockState();
            chunk.setBlockState(pos.set(sample.x(), y, sample.z()), state, false);
        }
        assertEquals(surface, chunk.getBlockState(pos.set(sample.x(), SEA_LEVEL - 1, sample.z())));
        Heightmap.primeHeightmaps(chunk, EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG));
        return chunk;
    }

    private static int requireOceanFloor(ProtoChunk chunk, Sample sample, int highestSolid)
    {
        final int oceanFloor = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, sample.x(), sample.z());
        assertTrue(oceanFloor == highestSolid || oceanFloor == highestSolid + 1, () -> "ocean floor " + oceanFloor + " for solid top " + highestSolid + " at " + sample);
        return oceanFloor;
    }

    private static RecordingParent apply(ProtoChunk chunk, Sample sample, BiomeExtension atollBiome, int preVolcanicHeight, boolean parentWritesSalt)
    {
        final RecordingParent parent = new RecordingParent(parentWritesSalt);
        final SurfaceBuilder builder = new AtollSurfaceBuilder(parent, Seed.of(LEVEL_SEED));
        final ChunkData chunkData = new ChunkData(chunk.getPos());
        chunkData.generatePartial(
            new LerpFloatLayer(100, 100, 100, 100),
            new LerpFloatLayer(0, 0, 0, 0),
            new LerpFloatLayer(0, 0, 0, 0),
            new LerpFloatLayer(20, 20, 20, 20),
            ForestType.GRASSLAND
        );
        final SurfaceBuilderContext context = new SurfaceBuilderContext(
            (LevelAccessor) null,
            chunk,
            chunkData,
            RandomSource.create(0),
            rockLayers(),
            SEA_LEVEL,
            MIN_Y,
            TFCBiomes.OCEAN,
            TFCBiomes.OCEAN,
            TFCBiomes.OCEAN,
            atollBiome,
            TFCBiomes.OCEAN
        );
        context.buildSurface(atollBiome, atollBiome, 1, true, builder, sample.x(), SEA_LEVEL + 24, sample.z(), 0, preVolcanicHeight);
        return parent;
    }

    private static BlockState at(ProtoChunk chunk, Sample sample, int y)
    {
        return chunk.getBlockState(new BlockPos(sample.x(), y, sample.z()));
    }

    private static BlockState saltWater()
    {
        return TFCFluids.SALT_WATER.createSourceBlock();
    }

    private static BlockState freshWater()
    {
        return Fluids.WATER.getSource().defaultFluidState().createLegacyBlock();
    }

    private static BlockState raw(Rock rock)
    {
        return TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.RAW).get().defaultBlockState();
    }

    private static BlockState expectedSand(Cellular2D.Cell cell)
    {
        // Chunk rainfall is fixed at 100, below the pink-sand threshold.
        if (Helpers.hashDouble(cell.noise(), 6324) > 0.7)
        {
            return TFCBlocks.SAND.get(SandBlockType.YELLOW).get().defaultBlockState();
        }
        return TFCBlocks.SAND.get(SandBlockType.WHITE).get().defaultBlockState();
    }

    private static BlockState expectedRock(Cellular2D.Cell cell)
    {
        return Helpers.hashDouble(cell.noise(), 624) > 0.7 ? raw(Rock.DOLOMITE) : raw(Rock.LIMESTONE);
    }

    private static synchronized Registry<Biome> biomes()
    {
        if (biomes == null)
        {
            final MappedRegistry<Biome> registry = new MappedRegistry<>(Registries.BIOME, Lifecycle.stable());
            final Biome plains = new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(7907327).build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build();
            registry.register(Biomes.PLAINS, plains, RegistrationInfo.BUILT_IN);
            registry.freeze();
            biomes = registry;
        }
        return biomes;
    }

    private static synchronized RockLayerSettings rockLayers()
    {
        if (rockLayers == null)
        {
            final Block block = raw(Rock.GRANITE).getBlock();
            final RockSettings settings = new RockSettings(block, block, block, block, block, block, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
            rockLayers = RockLayerSettings.decode(new RockLayerSettings.Data(
                Map.of("granite", Holder.direct(settings)),
                List.of("granite"),
                List.of(),
                List.of("bottom"),
                List.of("bottom"),
                List.of("bottom"),
                List.of("bottom")
            )).getOrThrow(IllegalStateException::new);
        }
        return rockLayers;
    }

    private static synchronized Sample raisedEnclosed()
    {
        if (raisedEnclosed == null)
        {
            raisedEnclosed = firstAboveGate(catalog().enclosed());
        }
        return raisedEnclosed;
    }

    private static synchronized Sample raisedOpen()
    {
        if (raisedOpen == null)
        {
            raisedOpen = firstAboveGate(catalog().open());
        }
        return raisedOpen;
    }

    private static Sample firstAboveGate(List<Sample> samples)
    {
        for (Sample sample : samples)
        {
            final ProtoChunk chunk = lagoon(sample, RAISED_FLOOR, saltWater());
            final int oceanFloor = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, sample.x(), sample.z());
            if ((oceanFloor == RAISED_FLOOR || oceanFloor == RAISED_FLOOR + 1)
                && oceanFloor > sample.volcanoHeight() + 2
                && RAISED_FLOOR > SEA_LEVEL - 11 + sample.randomHeight())
            {
                return sample;
            }
        }
        fail("no atoll column with oceanFloor above the terrain gate");
        return samples.get(0);
    }

    private static synchronized Catalog catalog()
    {
        if (catalog == null)
        {
            catalog = Catalog.scan();
        }
        return catalog;
    }

    private static OpenSimplex2D heightNoise(Seed seed)
    {
        // Matches AtollSurfaceBuilder's constructor, which consumes the first sequential seed.
        return new OpenSimplex2D(seed.next()).octaves(2).spread(0.1f).scaled(-4, 4);
    }

    private static Sample sampleAt(CenteredFeatureNoiseSampler sampler, OpenSimplex2D heightNoise, float frequency, int x, int z)
    {
        final float easing = sampler.calculateEasing(x, z, frequency);
        final Cellular2D.Cell cell = sampler.getCellularNoise().cell(x, z);
        final double randomHeight = heightNoise.noise(x, z);
        final int volcanoHeight = (int) Mth.clampedMap(easing, 0.4, 0.7, SEA_LEVEL - 70, SEA_LEVEL - 8 + randomHeight);
        return new Sample(x, z, easing, CenteredFeatureNoise.getAtollIntegrity(cell), randomHeight, volcanoHeight, cell);
    }

    private record Sample(int x, int z, float easing, double integrity, double randomHeight, int volcanoHeight, Cellular2D.Cell cell)
    {
        @Override
        public String toString()
        {
            return "x=" + x + ",z=" + z + ",easing=" + easing + ",integrity=" + integrity + ",volcano=" + volcanoHeight + ",randomHeight=" + randomHeight;
        }
    }

    private record Catalog(List<Sample> enclosed, List<Sample> open, Sample outside, Sample rare)
    {
        private static Catalog scan()
        {
            final OpenSimplex2D heightNoise = heightNoise(Seed.of(LEVEL_SEED));
            final CenteredFeatureNoiseSampler sampler = CenteredFeatureNoise.atolls(Seed.of(LEVEL_SEED));
            final float frequency = TFCBiomes.OCEAN_ATOLLS.getCenteredFeatureFrequency();
            final Cellular2D cells = sampler.getCellularNoise();
            final List<Sample> enclosed = new ArrayList<>();
            final List<Sample> open = new ArrayList<>();
            Sample rare = null;
            final Set<Long> seen = new HashSet<>();

            for (int x = 0; x <= 16000; x += 128)
            {
                for (int z = 0; z <= 16000; z += 128)
                {
                    final Cellular2D.Cell probed = cells.cell(x, z);
                    final long key = (((long) probed.cx()) << 32) ^ (probed.cy() & 0xffffffffL);
                    if (!seen.add(key))
                    {
                        continue;
                    }
                    final int cx = Mth.floor(probed.x());
                    final int cz = Mth.floor(probed.y());
                    if (cells.cell(cx, cz).cx() != probed.cx() || cells.cell(cx, cz).cy() != probed.cy())
                    {
                        continue;
                    }
                    final Sample sample = sampleAt(sampler, heightNoise, frequency, cx, cz);
                    if (Math.abs(sample.cell().noise()) > frequency)
                    {
                        if (rare == null && sample.easing() == 0)
                        {
                            rare = sample;
                        }
                        continue;
                    }
                    if (sample.easing() > 0.58 && sample.integrity() >= 1)
                    {
                        enclosed.add(sample);
                    }
                    else if (sample.easing() > 0.58 && sample.integrity() < 1)
                    {
                        open.add(sample);
                    }
                }
            }

            Sample outside = null;
            for (Sample sample : enclosed)
            {
                outside = walkOutside(sampler, heightNoise, frequency, sample);
                if (outside != null)
                {
                    break;
                }
            }
            assertFalse(enclosed.isEmpty(), "no enclosed atoll");
            assertFalse(open.isEmpty(), "no open atoll");
            assertNotNull(outside, "no position outside the lagoon threshold");
            assertNotNull(rare, "no cell that fails atoll rarity");
            return new Catalog(List.copyOf(enclosed), List.copyOf(open), outside, rare);
        }

        private static Sample walkOutside(CenteredFeatureNoiseSampler sampler, OpenSimplex2D heightNoise, float frequency, Sample origin)
        {
            final Cellular2D cells = sampler.getCellularNoise();
            for (int dx = 1; dx < 800; dx++)
            {
                final int x = origin.x() + dx;
                final Cellular2D.Cell cell = cells.cell(x, origin.z());
                if (cell.cx() != origin.cell().cx() || cell.cy() != origin.cell().cy())
                {
                    return null;
                }
                final float easing = sampler.calculateEasing(x, origin.z(), frequency);
                if (easing > 0 && easing <= 0.58)
                {
                    return sampleAt(sampler, heightNoise, frequency, x, origin.z());
                }
            }
            return null;
        }
    }

    private static final class RecordingParent implements SurfaceBuilder
    {
        private final boolean replaceSurfaceWithSalt;
        private int calls;

        private RecordingParent(boolean replaceSurfaceWithSalt)
        {
            this.replaceSurfaceWithSalt = replaceSurfaceWithSalt;
        }

        @Override
        public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
        {
            calls++;
            if (replaceSurfaceWithSalt)
            {
                context.setBlockState(context.getSeaLevel() - 1, saltWater());
            }
        }

        private int calls()
        {
            return calls;
        }
    }
}
