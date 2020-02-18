package net.dries007.tfc.world.gen.surfacebuilders;

import java.util.Random;

import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.world.gen.rock.RockData;

public class CompositeSurfaceBuilder implements ISurfaceBuilder
{
    private final float[] thresholds;
    private final ISurfaceBuilder[] builders;
    private final ISurfaceBuilder otherwise;
    private final boolean useRainfall;

    public CompositeSurfaceBuilder(float mid, ISurfaceBuilder low, ISurfaceBuilder high, boolean useRainfall)
    {
        this.thresholds = new float[] {mid};
        this.builders = new ISurfaceBuilder[] {low};
        this.otherwise = high;
        this.useRainfall = useRainfall;
    }

    public CompositeSurfaceBuilder(float min, float max, ISurfaceBuilder low, ISurfaceBuilder mid, ISurfaceBuilder high, boolean useRainfall)
    {
        this.thresholds = new float[] {min, max};
        this.builders = new ISurfaceBuilder[] {low, mid};
        this.otherwise = high;
        this.useRainfall = useRainfall;
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, RockData data, int x, int z, int startHeight, float temperature, float rainfall, float noise)
    {
        for (int i = 0; i < thresholds.length; i++)
        {
            float value = useRainfall ? rainfall : temperature;
            if (value < thresholds[i])
            {
                builders[i].buildSurface(random, chunkIn, data, x, z, startHeight, temperature, rainfall, noise);
                return;
            }
        }
        otherwise.buildSurface(random, chunkIn, data, x, z, startHeight, temperature, rainfall, noise);
    }
}
