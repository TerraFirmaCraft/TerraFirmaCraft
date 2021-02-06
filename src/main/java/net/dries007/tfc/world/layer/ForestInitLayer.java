package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public enum ForestInitLayer implements IAreaTransformer0
{
    INSTANCE;

    private final INoise2D forestBaseNoise;

    ForestInitLayer()
    {
        forestBaseNoise = new OpenSimplex2D(2).spread(0.3f);
    }

    @Override
    public int applyPixel(INoiseRandom context, int x, int z)
    {
        final float noise = forestBaseNoise.noise(x, z);
        if (noise < 0)
        {
            return TFCLayerUtil.FOREST_NONE;
        }
        else
        {
            return TFCLayerUtil.FOREST_NORMAL;
        }
    }
}
