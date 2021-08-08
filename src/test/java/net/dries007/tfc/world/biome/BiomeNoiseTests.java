package net.dries007.tfc.world.biome;

import net.dries007.tfc.Artist;
import net.dries007.tfc.world.noise.Noise2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class BiomeNoiseTests
{
    static final Artist.Noise<Noise2D> ARTIST = Artist.<Noise2D>forNoise(noise -> Artist.NoisePixel.coerceFloat(noise::noise)).centerSized(200);
    static final long seed = System.currentTimeMillis();

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
}
