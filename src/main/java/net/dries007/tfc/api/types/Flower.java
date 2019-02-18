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

public class Flower extends IForgeRegistryEntry.Impl<Flower>
{
    private final float dominance;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final float minDensity;
    private final float maxDensity;

    /**
     * Addon mods that want to add flowers should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     * When using this class, use the provided Builder to create your flowers. This will require all the default values, as well as
     * provide optional values that you can change
     *
     * @param name       the ResourceLocation registry name of this flower
     * @param minTemp    min temperature
     * @param maxTemp    max temperature
     * @param minRain    min rainfall
     * @param maxRain    max rainfall
     * @param minDensity min density. Use -1 to get all density values. 0.1 is the default, to create really low density regions of no flowers
     * @param maxDensity max density. Use 2 to get all density values
     * @param dominance  how much this flower is chosen over other flowers. Range 0 <> 10 with 10 being the most common
     */
    public Flower(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minRain, float maxRain, float minDensity, float maxDensity, float dominance)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.dominance = dominance;
        this.minDensity = minDensity;
        this.maxDensity = maxDensity;

        setRegistryName(name);
    }

    public boolean isValidLocation(float temp, float rain, float density)
    {
        return minTemp <= temp && maxTemp >= temp && minRain <= rain && maxRain >= rain && minDensity <= density && maxDensity >= density;
    }

    public float getDominance()
    {
        return dominance;
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
        private float minDensity;
        private float maxDensity;
        private float dominance;
        private ResourceLocation name;

        public Builder(@Nonnull ResourceLocation name, float minRain, float maxRain, float minTemp, float maxTemp)
        {
            this.minTemp = minTemp; // required values
            this.maxTemp = maxTemp;
            this.minRain = minRain;
            this.maxRain = maxRain;
            this.name = name;
            this.dominance = 0.001f * (maxTemp - minTemp) * (maxRain - minRain);
            this.minDensity = 0.1f;
            this.maxDensity = 2f;
        }

        public Flower.Builder setDensity(float min, float max)
        {
            this.minDensity = min;
            this.maxDensity = max;
            return this;
        }

        public Flower.Builder setDominance(float dom)
        {
            this.dominance = dom;
            return this;
        }

        public Flower build()
        {
            return new Flower(name, minTemp, maxTemp, minRain, maxRain, minDensity, maxDensity, dominance);
        }
    }
}
