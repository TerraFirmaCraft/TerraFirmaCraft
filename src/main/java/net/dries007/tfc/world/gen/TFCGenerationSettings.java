/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen;

import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.storage.WorldInfo;

public class TFCGenerationSettings extends GenerationSettings implements IBiomeProviderSettings
{
    public TFCGenerationSettings() {}

    public TFCGenerationSettings(WorldInfo worldInfo)
    {
        setWorldInfo(worldInfo);
    }

    public boolean isFlatBedrock()
    {
        return false; // todo: config
    }

    private WorldInfo worldInfo;

    public int getIslandFrequency()
    {
        return 6; // todo: config
    }

    public int getBiomeZoomLevel()
    {
        return 4; // todo: config
    }

    public WorldInfo getWorldInfo()
    {
        return worldInfo;
    }

    public void setWorldInfo(WorldInfo worldInfo)
    {
        this.worldInfo = worldInfo;
    }
}
