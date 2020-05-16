/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.storage.WorldInfo;

import net.dries007.tfc.config.TFCConfig;

public class TFCGenerationSettings extends GenerationSettings implements IBiomeProviderSettings
{
    private WorldInfo worldInfo;

    public TFCGenerationSettings() {}

    public TFCGenerationSettings(WorldInfo worldInfo)
    {
        setWorldInfo(worldInfo);
    }

    public boolean isFlatBedrock()
    {
        return TFCConfig.COMMON.flatBedrock.get();
    }

    public int getIslandFrequency()
    {
        return TFCConfig.COMMON.islandFrequency.get();
    }

    public int getBiomeZoomLevel()
    {
        return TFCConfig.COMMON.biomeZoomLevel.get();
    }

    public int getRockZoomLevel(int layer)
    {
        switch (layer)
        {
            case 0:
                return TFCConfig.COMMON.rockBottomZoomLevel.get();
            case 1:
                return TFCConfig.COMMON.rockMiddleZoomLevel.get();
            case 2:
                return TFCConfig.COMMON.rockTopZoomLevel.get();
        }
        throw new IllegalArgumentException("Unknown rock layer: " + layer);
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
