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
    public int applyPixel(INoiseRandom context, IArea mainArea, IArea lakeArea, int x, int z)
    {
        int mainValue = mainArea.get(getParentX(x), getParentY(z));
        int lakeValue = lakeArea.get(getParentX(x), getParentY(z));
        if (lakeValue == LAKE_MARKER && TFCLayerUtil.hasLake(mainValue))
        {
            return TFCLayerUtil.lakeFor(mainValue);
        }
        return mainValue;
    }
}
