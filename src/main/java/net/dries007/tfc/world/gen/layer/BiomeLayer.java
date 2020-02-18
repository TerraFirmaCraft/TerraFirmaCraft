/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import static net.dries007.tfc.world.gen.layer.TFCLayerUtil.*;

@ParametersAreNonnullByDefault
public enum BiomeLayer implements IC0Transformer
{
    INSTANCE;

    public static final int[] LOW_BIOMES = new int[] {PLAINS, HILLS, LOW_CANYONS, LOWLANDS, HILLS};
    public static final int[] MID_BIOMES = new int[] {ROLLING_HILLS, OLD_MOUNTAINS, BADLANDS, HILLS, PLAINS};
    public static final int[] HIGH_BIOMES = new int[] {PLATEAU, BADLANDS, MOUNTAINS, FLOODED_MOUNTAINS};

    @Override
    public int apply(INoiseRandom context, int value)
    {
        if (value == DEEP_OCEAN)
        {
            if (context.random(8) == 0)
            {
                return DEEP_OCEAN_RIDGE;
            }
            else
            {
                return DEEP_OCEAN;
            }
        }
        else if (value == PLAINS)
        {
            return LOW_BIOMES[context.random(LOW_BIOMES.length)];
        }
        else if (value == TFCLayerUtil.HILLS)
        {
            return MID_BIOMES[context.random(MID_BIOMES.length)];
        }
        else if (value == TFCLayerUtil.MOUNTAINS)
        {
            return HIGH_BIOMES[context.random(HIGH_BIOMES.length)];
        }
        return 0;
    }
}
