/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER;

public enum MixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer
{
    INSTANCE;

    public int apply(INoiseRandom context, IArea mainArea, IArea riverArea, int x, int z)
    {
        int mainValue = mainArea.getValue(getOffsetX(x), getOffsetZ(z));
        int riverValue = riverArea.getValue(getOffsetX(x), getOffsetZ(z));
        if (TFCLayerUtil.isOcean(mainValue) || mainValue == TFCLayerUtil.FLOODED_MOUNTAINS || mainValue == TFCLayerUtil.LAKE)
        {
            return mainValue;
        }
        else if (riverValue == RIVER)
        {
            return riverValue;
        }
        else
        {
            return mainValue;
        }
    }
}