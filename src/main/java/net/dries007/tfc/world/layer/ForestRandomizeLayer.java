package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum ForestRandomizeLayer implements IC0Transformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int value)
    {
        if (value == FOREST_NONE)
        {
            final int random = context.random(16);
            if (random <= 2)
            {
                return FOREST_SPARSE;
            }
            else if (random == 3)
            {
                return FOREST_NORMAL;
            }
        }
        else if (value == FOREST_SPARSE)
        {
            final int random = context.random(7);
            if (random <= 3)
            {
                return FOREST_NORMAL;
            }
        }
        else if (value == FOREST_NORMAL || value == FOREST_OLD)
        {
            final int random = context.random(24);
            if (random == 1 && value != FOREST_OLD)
            {
                return FOREST_SPARSE;
            }
            else if (random == 2)
            {
                return FOREST_NONE;
            }
            else if (random <= 6)
            {
                return FOREST_OLD;
            }
        }
        return value;
    }
}
