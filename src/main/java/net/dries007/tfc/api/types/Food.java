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
    private final float waterContent;
    private final float heal;
    private final float saturation;
    private boolean edible;
    private final float decayRate;

    /**
     * This is a registry object that will create a number of things:
     * 1. Crop blocks and seedbags
     * 2. A crop object to be used in TFC world gen
     *
     * Addon mods that want to add crops should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     *
     * @param name             The ResourceLocation registry name of this food
     * @param mineral          Mineral content of this food (calcium)
     * @param carb             Carbohydrate content of this food
     * @param fat              Fat content of this food
     * @param protein          Protein content of this food
     * @param vitamin          Vitamin content of this food
     * @param waterContent     Water content of this food, can be negative(will contribute to filling thirst bar)
     * @param heal             A vanilla parameter for how much food haunches
     * @param saturation       How much the food contributes to saturation, only meals will contribute
     * @param edible           Can it be eaten
     * @param decayRate        Rate of decay

     */


    public Food(@Nonnull ResourceLocation name, float mineral, float carb, float fat, float protein, float vitamin, float waterContent, float heal, float saturation, boolean edible, float decayRate)
    {
        this.mineral = mineral;
        this.carb = carb;
        this.fat = fat;
        this.protein = protein;
        this.vitamin = vitamin;
        this.waterContent = waterContent;
        this.heal = heal;
        this.saturation = saturation;
        this.edible = edible;
        this.decayRate = decayRate;


        setRegistryName(name);
    }

    public float getMineral() { return mineral; }

    public float getCarb() { return carb; }

    public float getFat() { return fat; }

    public float getProtein() { return protein; }

    public float getVitamin() { return vitamin; }

    public float getWaterContent() { return waterContent; }

    public float getHeal() { return heal; }

    public float getSaturation() { return saturation; }

    public boolean isEdible() { return edible; }

    public float getDecayRate() { return decayRate; }



    @Override
    public String toString() { return String.valueOf(getRegistryName()); }

    public static class Builder
    {
        private float mineral;
        private float carb;
        private float fat;
        private float protein;
        private float vitamin;
        private float waterContent;
        private float heal;
        private float saturation;
        private boolean edible;
        private float decayRate;

        private ResourceLocation name;

        public Builder(@Nonnull ResourceLocation name, float mineral, float carb, float fat, float protein, float vitamin, float waterContent, float heal, float saturation, boolean edible, float decayRate)
        {
            this.name = name;
            this.mineral = mineral;
            this.carb = carb;
            this.fat = fat;
            this.protein = protein;
            this.vitamin = vitamin;
            this.waterContent = waterContent;
            this.heal = heal;
            this.saturation = saturation;
            this.edible = edible;
            this.decayRate = decayRate;

        }

        public Food build()
        {
            return new Food(name, mineral, carb, fat, protein, vitamin, waterContent, heal, saturation, edible, decayRate);
        }
    }
}
