/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.visualizations;

import java.awt.*;
import java.util.List;
import java.util.*;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.TFCChunkDataGenerator;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.settings.RockLayer;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class RockLayerSettingsVisualizations extends TestHelper
{
    @Test
    public void testOldRockLayers()
    {
        final long seed = seed();
        final Random random = new Random(seed);
        final Map<ResourceLocation, RockSettings> map = new HashMap<>();
        @SuppressWarnings("deprecation") final List<Block> blocks = Registry.BLOCK.stream().toList();
        for (int i = 0; i < 20; i++)
        {
            final ResourceLocation id = Helpers.identifier("rock_" + i);
            final Block block = blocks.get(i);
            map.put(id, new RockSettings(id, block, block, block, block, block, block, Optional.empty(), Optional.empty(), true, true, true));
        }
        final RockLayerSettings settings = new RockLayerSettings(map, 1);
        final List<RockSettings> rocks = settings.getRocks();
        final ConcurrentArea<RockSettings> bottomRockLayer = TFCChunkDataGenerator.createRockLayer(random, settings, settings.getRocksForLayer(RockLayer.BOTTOM));
        final ConcurrentArea<RockSettings> middleRockLayer = TFCChunkDataGenerator.createRockLayer(random, settings, settings.getRocksForLayer(RockLayer.MIDDLE));
        final ConcurrentArea<RockSettings> topRockLayer = TFCChunkDataGenerator.createRockLayer(random, settings, settings.getRocksForLayer(RockLayer.TOP));
        final Noise2D layerHeightNoise = new OpenSimplex2D(random.nextInt()).octaves(2).scaled(-10, 10).spread(0.03f);

        final Artist.Colored<?> artist = Artist.forColor(area -> (x, y) -> {
            final int x0 = (int) x;
            final int y0 = (int) (320 - y);
            final int z0 = 0;
            final int sh = (int) Mth.clamp(384 * x / 1000f - 64, 30, 200);
            final int rh = (int) layerHeightNoise.noise(x0, z0);

            RockSettings rock;
            if (y0 > (int) (TFCChunkGenerator.SEA_LEVEL_Y + 46 - 0.2 * sh + rh))
            {
                rock = topRockLayer.get(x0, z0);
            }
            else if (y0 > (int) (TFCChunkGenerator.SEA_LEVEL_Y - 14 - 0.2 * sh + rh))
            {
                rock = middleRockLayer.get(x0, z0);
            }
            else
            {
                rock = bottomRockLayer.get(x0, z0);
            }

            if (y0 > sh)
            {
                return y0 > TFCChunkGenerator.SEA_LEVEL_Y ?
                    new Color(150, 150, 250) :
                    new Color(210, 210, 250);
            }

            return Artist.Colors.RANDOM_INT.apply(rocks.indexOf(rock));
        }).dimensionsSized(1000, 384);

        artist.draw("rock_profile");
    }
}
