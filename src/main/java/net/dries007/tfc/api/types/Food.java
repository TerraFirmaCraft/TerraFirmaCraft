/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Food extends IForgeRegistryEntry.Impl<Food>
{
    private final float mineral;
    private final float carb;
    private final float fat;
    private final float protein;
    private final float vitamin;
    private final float water;
    private final float heal;
    private final float saturation;
    private boolean edible;
    private int decay;

    private ResourceLocation name;

    public Food(@Nonnull ResourceLocation name, float mineral, float carb, float fat, float protein, float vitamin, float water, float heal, float saturation, boolean edible, int decay)
    {
        this.name = name;
        this.mineral = mineral;
        this.carb = carb;
        this.fat = fat;
        this.protein = protein;
        this.vitamin = vitamin;
        this.water = water;
        this.heal = heal;
        this.saturation = saturation;
        this.edible = edible;
        this.decay = decay;


        setRegistryName(name);
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
        private boolean pickable;
        private float maxLifespan;
    }

    public Food build()
    {
        return new Food(name, mineral, carb, fat, protein, vitamin, water, heal, saturation, edible, decay);
    }
}
