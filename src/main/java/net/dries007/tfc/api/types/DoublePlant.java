/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DoublePlant extends IForgeRegistryEntry.Impl<DoublePlant>
{
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;

    /**
     * Addon mods that want to add flowers should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     * When using this class, use the provided Builder to create your flowers. This will require all the default values, as well as
     * provide optional values that you can change
     *
     * @param name    the ResourceLocation registry name of this flower
     * @param minTemp min temperature
     * @param maxTemp max temperature
     * @param minRain min rainfall
     * @param maxRain max rainfall
     */
    public DoublePlant(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minRain, float maxRain)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;

        setRegistryName(name);
    }

    public boolean isValidLocation(float temp, float rain)
    {
        return minTemp <= temp && maxTemp >= temp && minRain <= rain && maxRain >= rain;
    }

    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public static class Builder
    {
        private float minTemp;
        private float maxTemp;
        private float minRain;
        private float maxRain;
        private ResourceLocation name;

        public Builder(@Nonnull ResourceLocation name, float minRain, float maxRain, float minTemp, float maxTemp)
        {
            this.minTemp = minTemp; // required values
            this.maxTemp = maxTemp;
            this.minRain = minRain;
            this.maxRain = maxRain;
            this.name = name;
        }

        public DoublePlant build()
        {
            return new DoublePlant(name, minTemp, maxTemp, minRain, maxRain);
        }
    }
}
