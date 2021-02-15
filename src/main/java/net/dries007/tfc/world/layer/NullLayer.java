/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
    public int apply(INoiseRandom context, int x, int z)
    {
        return TFCLayerUtil.NULL_MARKER;
    }
}
