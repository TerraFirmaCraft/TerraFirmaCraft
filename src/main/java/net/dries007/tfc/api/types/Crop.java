/*
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.world.classic.CalenderTFC;

import static net.dries007.tfc.world.classic.CalenderTFC.TICKS_IN_DAY;

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
    private final boolean pickable;
    private final float maxLifespan;
    private final Food foodItem;


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
     * @param pickable         crop can be picked with right click allowing multiple harvests
     * @param lifespan         crops lifespan, after this many ticks it will die
     * @param foodItem         the food item dropped by mature crop

     */

    public Crop(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minGrowthTemp, float maxGrowthTemp, float minRain, float maxRain, float minDensity, float maxDensity, int growthStages, float minStageGrowthTime, boolean pickable, float lifespan, @Nullable Food foodItem)
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
        this.pickable = pickable;
        this.maxLifespan = lifespan;
        this.foodItem = foodItem;


        setRegistryName(name);
    }

    public boolean isValidLocation(float temp, float rain, float density)
    {
        return minTemp <= temp && maxTemp >= temp && minRain <= rain && maxRain >= rain && minDensity <= density && maxDensity >= density;
    }

    public int getGrowthStages()
    {
        return growthStages;
    }

    public float getMinStageGrowthTime()
    {
        return minStageGrowthTime;
    }

    public float getMinGrowthTemp()
    {
        return minGrowthTemp;
    }

    public float getMaxGrowthTemp()
    {
        return maxGrowthTemp;
    }

    public boolean isPickable()
    {
        return pickable;
    }

    public float getMaxLifespan()
    {
        return maxLifespan;
    }

    public Food getFoodItem() { return foodItem; }

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
        private boolean pickable;
        private float maxLifespan;
        private float minDensity;
        private float maxDensity;
        private final Food foodItem;


        private ResourceLocation name;

        public Builder(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minGrowthTemp, float maxGrowthTemp, float minRain, float maxRain, int growthStages, float minStageGrowthTime, boolean pickable, float lifespan, @Nonnull ResourceLocation foodItem)
        {
            this.name = name;
            this.minTemp = minTemp; // required values
            this.maxTemp = maxTemp;
            this.minGrowthTemp = minGrowthTemp;
            this.maxGrowthTemp = maxGrowthTemp;
            this.minRain = minRain;
            this.maxRain = maxRain;
            this.growthStages = growthStages;
            this.minStageGrowthTime = minStageGrowthTime;
            this.pickable = pickable;
            this.maxLifespan = (CalenderTFC.getDaysInMonth() * TICKS_IN_DAY) * lifespan;
            this.minDensity = 0.1f; // default values
            this.maxDensity = 2f;
            this.foodItem = TFCRegistries.FOODS.getValue(foodItem);

        }

        public Builder setDensity(float min, float max)
        {
            this.minDensity = min;
            this.maxDensity = max;
            return this;
        }

        public Crop build()
        {
            return new Crop(name, minTemp, maxTemp, minGrowthTemp, maxGrowthTemp, minRain, maxRain, minDensity, maxDensity, growthStages, minStageGrowthTime, pickable, maxLifespan, foodItem);
        }

    }
}
