/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.visualizations;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class BiomeNoiseVisualizations extends TestHelper
{
    static final Artist.Noise<Noise2D> ARTIST = Artist.<Noise2D>forNoise(noise -> Artist.NoisePixel.coerceFloat(noise::noise)).centerSized(200);
    static final long seed = System.currentTimeMillis();

    @Test
    public void testFoo()
    {
        ARTIST.centerSized(1000).draw("badlands_height", new OpenSimplex2D(seed).octaves(2).scaled(0, 6).spread(0.0014f).terraces(6));
        ARTIST.centerSized(1000).draw("badlands_style", new OpenSimplex2D(seed).octaves(2).scaled(-0.2f, 1.2f).spread(0.0003f).clamped(0, 1));
    }

    @Test
    public void testBadlands()
    {
        ARTIST.draw("biome_noise_badlands", BiomeNoise.badlands(seed));
    }

    @Test
    public void testCanyons()
    {
        ARTIST.draw("biome_noise_canyons", BiomeNoise.canyons(seed, 0, 1));
    }

    @Test
    public void testHills()
    {
        ARTIST.draw("biome_noise_hills", BiomeNoise.hills(seed, 0, 1));
    }

    @Test
    public void testLake()
    {
        ARTIST.draw("biome_noise_lake", BiomeNoise.lake(seed));
    }

    @Test
    public void testRiver()
    {
        ARTIST.draw("biome_noise_river", BiomeNoise.river(seed));
    }

    @Test
    public void testLowlands()
    {
        ARTIST.draw("biome_noise_lowlands", BiomeNoise.lowlands(seed));
    }

    @Test
    public void testMountains()
    {
        ARTIST.draw("biome_noise_mountains", BiomeNoise.mountains(seed, 10, 70));
    }

    @Test
    public void testOcean()
    {
        ARTIST.draw("biome_noise_ocean", BiomeNoise.ocean(seed, -20, -12));
    }

    @Test
    public void testOceanRidge()
    {
        ARTIST.draw("biome_noise_ocean_ridge", BiomeNoise.oceanRidge(seed, -20, -12));
    }

    @Test
    public void testShore()
    {
        ARTIST.draw("biome_noise_shore", BiomeNoise.shore(seed));
    }

    @Test
    public void testBryceCanyon()
    {
        ARTIST.draw("bryce_canyon", BiomeNoise.bryceCanyon(seed));
    }
}
