/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome.provider;

import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.storage.WorldInfo;

import net.dries007.tfc.world.gen.TFCGenerationSettings;

public class TFCBiomeProviderSettings implements IBiomeProviderSettings
{
    private WorldInfo worldInfo;
    private TFCGenerationSettings settings;

    public int getIslandFrequency()
    {
        return 12; // todo: config
    }

    public int getBiomeZoomLevel()
    {
        return 3; // todo: config
    }

    public WorldInfo getWorldInfo()
    {
        return worldInfo;
    }

    public void setWorldInfo(WorldInfo worldInfo)
    {
        this.worldInfo = worldInfo;
    }

    public TFCGenerationSettings getGeneratorSettings()
    {
        return settings;
    }

    public void setGeneratorSettings(TFCGenerationSettings settings)
    {
        this.settings = settings;
    }
}
