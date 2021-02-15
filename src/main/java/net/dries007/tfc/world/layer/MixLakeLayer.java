/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.LAKE_MARKER;

/**
 * Mixes lakes into the standard biome layer
 */
public enum MixLakeLayer implements IAreaTransformer2, IDimOffset0Transformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, IArea mainArea, IArea lakeArea, int x, int z)
    {
        int mainValue = mainArea.getValue(getOffsetX(x), getOffsetZ(z));
        int lakeValue = lakeArea.getValue(getOffsetX(x), getOffsetZ(z));
        if (lakeValue == LAKE_MARKER && TFCLayerUtil.hasLake(mainValue))
        {
            return TFCLayerUtil.lakeFor(mainValue);
        }
        return mainValue;
    }
}
