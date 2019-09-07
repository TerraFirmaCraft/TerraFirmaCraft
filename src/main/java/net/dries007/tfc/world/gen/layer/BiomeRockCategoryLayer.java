/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

import net.dries007.tfc.world.gen.rock.RockCategory;

import static net.dries007.tfc.world.gen.layer.TFCLayerUtil.*;

public enum BiomeRockCategoryLayer implements IC1Transformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int value)
    {
        return apply(value, context.random(2) == 0).ordinal();
    }

    private RockCategory apply(int value, boolean randomChoice)
    {
        if (value == PLAINS || value == LOWLANDS || value == LOW_CANYONS)
        {
            // Low / Old -> Sed / Ign Int
            return randomChoice ? RockCategory.SEDIMENTARY : RockCategory.IGNEOUS_INTRUSIVE;
        }
        else if (value == HILLS || value == CANYONS || value == PLATEAU)
        {
            // Low / New -> Sed / Ign Ex
            return randomChoice ? RockCategory.SEDIMENTARY : RockCategory.IGNEOUS_EXTRUSIVE;
        }
        else if (value == FLOODED_MOUNTAINS || value == OLD_MOUNTAINS)
        {
            // High / Old -> Met / Ign Int
            return randomChoice ? RockCategory.METAMORPHIC : RockCategory.IGNEOUS_INTRUSIVE;
        }
        else if (value == BADLANDS || value == MOUNTAINS || value == ROLLING_HILLS)
        {
            // High / New -> Met / Ign Ex
            return randomChoice ? RockCategory.METAMORPHIC : RockCategory.IGNEOUS_EXTRUSIVE;
        }
        return RockCategory.IGNEOUS_EXTRUSIVE;
    }
}
