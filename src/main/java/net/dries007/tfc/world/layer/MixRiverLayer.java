/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER_MARKER;

public enum MixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer
{
    INSTANCE;

    public int applyPixel(INoiseRandom context, IArea mainArea, IArea riverArea, int x, int z)
    {
        int mainValue = mainArea.get(getParentX(x), getParentY(z));
        int riverValue = riverArea.get(getParentX(x), getParentY(z));
        if (riverValue == RIVER_MARKER && TFCLayerUtil.hasRiver(mainValue))
        {
            return TFCLayerUtil.riverFor(mainValue);
        }
        return mainValue;
    }
}