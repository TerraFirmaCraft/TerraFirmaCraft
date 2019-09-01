/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen;

import net.minecraft.world.gen.GenerationSettings;

public class TFCGenerationSettings extends GenerationSettings
{
    public boolean isFlatBedrock()
    {
        return false; // todo: config
    }

    public int getRockZoomLevel()
    {
        return 5; // todo: config
    }
}
