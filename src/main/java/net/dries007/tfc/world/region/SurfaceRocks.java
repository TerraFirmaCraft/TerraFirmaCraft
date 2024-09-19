/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkDataGenerator;
import net.dries007.tfc.world.chunkdata.RegionChunkDataGenerator;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.settings.Settings;

public enum SurfaceRocks implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final Settings settings = TFCChunkGenerator.settings();
        //todo: needs to use level seed to be accurate/in general I have no idea what I am doing here
        final long seed = 89893787674L;
        final RandomSource random = new XoroshiroRandomSource(seed);
        final ChunkDataGenerator chunkDataGenerator = RegionChunkDataGenerator.create(random.nextLong(), settings.rockLayerSettings(), regionGenerator);
        context.generator().setRockGenerator(chunkDataGenerator);

        for (final var point : region.points())
        {
            final RockSettings surfaceRock = context.generator().getSurfaceRock(point.x, point.z);
            point.surfaceRock = getSurfaceType(surfaceRock);
        }
    }

    private int getSurfaceType(RockSettings surfaceRock)
    {
        return surfaceRock.karst().isPresent() ? surfaceRock.karst().get() ? 2 : 1 : 0;
    }
}
