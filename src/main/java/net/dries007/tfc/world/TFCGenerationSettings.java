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

    public WorldInfo getWorldInfo()
    {
        return worldInfo;
    }

    public void setWorldInfo(WorldInfo worldInfo)
    {
        this.worldInfo = worldInfo;
    }
}
