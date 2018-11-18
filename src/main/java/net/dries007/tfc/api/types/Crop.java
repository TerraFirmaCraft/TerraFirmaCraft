/*
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Crop extends IForgeRegistryEntry.Impl<Crop>
{

    private final float minTemp;
    private final float maxTemp;
    private final float minGrowthTemp;
    private final float maxGrowthTemp;
    private final float minRain;
    private final float maxRain;
    private final int growthStages;
    private final float minStageGrowthTime;
    private final float minDensity;
    private final float maxDensity;


    /**
     * This is a registry object that will create a number of things:
     * 1. Crop blocks and seedbags
     * 2. A crop object to be used in TFC world gen
     *
     * Addon mods that want to add crops should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     *
     * @param name             the ResourceLocation registry name of this tree
     * @param minTemp          min temperature that crop will stay planted
     * @param maxTemp          max temperature that crop will stay planted
     * @param minGrowthTemp    min temperature crop will grow
     * @param maxGrowthTemp    max temperature crop will grow
     * @param minRain          min rainfall
     * @param maxRain          max rainfall
     * @param growthStages     the number of growth stages for crop
     * @param minStageGrowthTime    the amount of time (in in-game days) that this crop requires to grow one stage
     * @param minDensity       min density. Use -1 to get all density values. 0.1 is the default, to create really low density regions of no crops
     * @param maxDensity       max density. Use 2 to get all density values

     */

    public Crop(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minGrowthTemp, float maxGrowthTemp, float minRain, float maxRain, float minDensity, float maxDensity, int growthStages, float minStageGrowthTime)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minGrowthTemp = minGrowthTemp;
        this.maxGrowthTemp = maxGrowthTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.growthStages = growthStages;
        this.minStageGrowthTime = minStageGrowthTime;
        this.minDensity = minDensity;
        this.maxDensity = maxDensity;


        setRegistryName(name);
    }

    public boolean isValidLocation(float temp, float rain, float density)
    {
        return minGrowthTemp <= temp && maxGrowthTemp >= temp && minRain <= rain && maxRain >= rain && minDensity <= density && maxDensity >= density;
    }

    @Override
    public String toString() { return String.valueOf(getRegistryName()); }

    public static class Builder
    {
        private float minTemp;
        private float maxTemp;
        private float minGrowthTemp;
        private float maxGrowthTemp;
        private float minRain;
        private float maxRain;
        private int growthStages;
        private float minStageGrowthTime;
        private float minDensity;
        private float maxDensity;

        private ResourceLocation name;

        public Builder(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minGrowthTemp, float maxGrowthTemp, float minRain, float maxRain, int growthStages, float minStageGrowthTime)
        {
            this.name = name;
            this.minTemp = minTemp; // required values
            this.maxTemp = maxTemp;
            this.minRain = minRain;
            this.minGrowthTemp = minGrowthTemp;
            this.maxGrowthTemp = maxGrowthTemp;
            this.maxRain = maxRain;
            this.growthStages = growthStages;
            this.minStageGrowthTime = minStageGrowthTime;
            this.minDensity = 0.1f; // default values
            this.maxDensity = 2f;

        }

        public Builder setDensity(float min, float max)
        {
            this.minDensity = min;
            this.maxDensity = max;
            return this;
        }

        public Crop build()
        {
            return new Crop(name, minTemp, maxTemp, minGrowthTemp, maxGrowthTemp, minRain, maxRain, minDensity, maxDensity, growthStages, minStageGrowthTime);
        }

    }
}
