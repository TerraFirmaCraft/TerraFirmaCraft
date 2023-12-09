/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.chunkdata.ChunkRockDataCache;
import net.dries007.tfc.world.chunkdata.RegionChunkDataGenerator;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

public class RegionChunkDataGeneratorTest extends TestHelper
{
    @Test
    public void testGenerateRockLayerAtExactEdgeOfLayerHeightWithNoCache()
    {
        generator().generateRock(0, 90, 0, 100, null);
    }

    @Test
    public void testGenerateRockLayerAtExactEdgeOfLayerHeightWithCache()
    {
        generator().generateRock(0, 90, 0, 100, new ChunkRockDataCache(new ChunkPos(0, 0)));
    }

    @Test
    public void testGenerateRockLayerAboveSurfaceHeightWithNoCache()
    {
        generator().generateRock(0, 120, 0, 100, null);
    }

    @Test
    public void testGenerateRockLayerAboveSurfaceHeightWithCache()
    {
        generator().generateRock(0, 120, 0, 100, new ChunkRockDataCache(new ChunkPos(0, 0)));
    }

    private RegionChunkDataGenerator generator()
    {
        final RockLayerSettings rockLayerSettings = new RockLayerSettings.Data(
            Map.of("rock", new RockSettings(Blocks.STONE, Blocks.DEEPSLATE, null, null, null, null, Optional.empty(), Optional.empty(), Optional.empty())),
            List.of("rock"),
            List.of(new RockLayerSettings.LayerData("base", Map.of("rock", "bottom"))),
            List.of("base"),
            List.of("base"),
            List.of("base"),
            List.of("base")
        ).parse();
        final ThreadLocal<Area> rockLayerArea = ThreadLocal.withInitial(() -> new Area((x, z) -> 0, 1));
        return new RegionChunkDataGenerator(null, rockLayerSettings, null, rockLayerArea, (x, z) -> 10, (x, z) -> 1, (x, z) -> 2, (x, z) -> 0, (x, z) -> 0);
    }
}
