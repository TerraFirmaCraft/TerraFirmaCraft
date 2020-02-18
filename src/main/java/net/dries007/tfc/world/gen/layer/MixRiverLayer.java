/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

import static net.dries007.tfc.world.gen.layer.TFCLayerUtil.RIVER;

@ParametersAreNonnullByDefault
public enum MixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer
{
    INSTANCE;

    public int apply(INoiseRandom context, IArea mainArea, IArea riverArea, int x, int z)
    {
        int mainValue = mainArea.getValue(func_215721_a(x), func_215722_b(z));
        int riverValue = riverArea.getValue(func_215721_a(x), func_215722_b(z));
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