/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public class IslandLayer implements IAreaTransformer0
{
    private final int islandFrequency;

    public IslandLayer(int islandFrequency)
    {
        this.islandFrequency = islandFrequency;
    }

    @Override
    public int applyPixel(INoiseRandom random, int x, int z)
    {
        if (x == 0 && z == 0)
        {
            return TFCLayerUtil.PLAINS;
        }
        else
        {
            return random.nextRandom(islandFrequency) == 0 ? TFCLayerUtil.PLAINS : TFCLayerUtil.DEEP_OCEAN;
        }
    }
}