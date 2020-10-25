package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

/**
 * Initialize a layer filled with NULL_MARKER
 */
public enum NullLayer implements IAreaTransformer0
{
    INSTANCE;

    @Override
    public int applyPixel(INoiseRandom context, int x, int z)
    {
        return TFCLayerUtil.NULL_MARKER;
    }
}
