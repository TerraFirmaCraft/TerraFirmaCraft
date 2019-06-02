/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.plants.BlockCropTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;

public enum Crop implements ICrop
{
    BARLEY(Food.BARLEY, 5f, 35f, 100f, 400f, 8, 0.5f, false),
    MAIZE(Food.MAIZE, 5f, 35f, 100f, 400f, 6, 0.5f, false),
    OAT(Food.OAT, 5f, 35f, 100f, 400f, 8, 0.5f, false),
    RICE(Food.RICE, 5f, 35f, 100f, 400f, 8, 0.5f, false),
    RYE(Food.RYE, 5f, 35f, 100f, 400f, 8, 0.5f, false),
    WHEAT(Food.WHEAT, 5f, 35f, 100f, 400f, 8, 0.5f, false),
    BEET(Food.BEET, 5f, 35f, 100f, 400f, 7, 0.5f, false),
    CABBAGE(Food.CABBAGE, 5f, 35f, 100f, 400f, 6, 0.5f, false),
    CARROT(Food.CARROT, 5f, 35f, 100f, 400f, 5, 0.5f, false),
    GARLIC(Food.GARLIC, 5f, 35f, 100f, 400f, 5, 0.5f, false),
    GREEN_BEAN(Food.GREEN_BEAN, 5f, 35f, 100f, 400f, 7, 0.5f, false),
    ONION(Food.ONION, 5f, 35f, 100f, 400f, 7, 0.5f, false),
    POTATO(Food.POTATO, 5f, 35f, 100f, 400f, 7, 0.5f, false),
    SOYBEAN(Food.SOYBEAN, 5f, 35f, 100f, 400f, 7, 0.5f, false),
    SQUASH(Food.SQUASH, 5f, 35f, 100f, 400f, 7, 0.5f, false),
    SUGARCANE(Food.SUGARCANE, 5f, 35f, 100f, 400f, 8, 0.5f, false),
    RED_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.RED_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)), 5f, 35f, 100f, 400f, 7, 0.5f, false),
    TOMATO(Food.TOMATO, 5f, 35f, 100f, 400f, 8, 0.5f, false),
    YELLOW_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.YELLOW_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)), 5f, 35f, 100f, 400f, 7, 0.5f, false),
    JUTE(() -> new ItemStack(ItemsTFC.JUTE), () -> ItemStack.EMPTY, 5f, 35f, 100f, 400f, 6, 0.5f, false);

    private final Supplier<ItemStack> foodDrop;
    private final Supplier<ItemStack> foodDropEarly;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final int growthStages;
    private final float growthTime;
    private final boolean isPickable;

    Crop(Food foodDrop, float minTemp, float maxTemp, float minRain, float maxRain, int growthStages, float growthTime, boolean isPickable)
    {
        this(() -> new ItemStack(ItemFoodTFC.get(foodDrop)), () -> ItemStack.EMPTY, minTemp, maxTemp, minRain, maxRain, growthStages, growthTime, isPickable);
    }

    Crop(Supplier<ItemStack> foodDrop, Supplier<ItemStack> foodDropEarly, float minTemp, float maxTemp, float minRain, float maxRain, int growthStages, float growthTime, boolean isPickable)
    {
        this.foodDrop = foodDrop;
        this.foodDropEarly = foodDropEarly;

        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;

        this.growthStages = growthStages;
        this.growthTime = growthTime;
        this.isPickable = isPickable;
    }

    @Override
    public float getGrowthTime()
    {
        return growthTime;
    }

    @Override
    public int getMaxStage()
    {
        return growthStages - 1;
    }

    @Override
    public boolean isValidConditions(float temperature, float rainfall)
    {
        return minTemp - 5 < temperature && temperature < maxTemp + 5 && minRain - 50 < rainfall && rainfall < maxRain + 50;
    }

    @Override
    public boolean isValidForGrowth(float temperature, float rainfall)
    {
        return minTemp < temperature && temperature < maxTemp && minRain < rainfall && rainfall < maxRain;
    }

    @Override
    public boolean isPickable()
    {
        return isPickable;
    }

    @Nonnull
    @Override
    public ItemStack getFoodDrop(int currentStage)
    {
        if (currentStage == getMaxStage())
        {
            return foodDrop.get();
        }
        else if (currentStage == getMaxStage() - 1)
        {
            return foodDropEarly.get();
        }
        return ItemStack.EMPTY;
    }

    public BlockCropTFC create()
    {
        switch (growthStages)
        {
            case 5:
                return new BlockCropTFC.Simple5(this);
            case 6:
                return new BlockCropTFC.Simple6(this);
            case 7:
                return new BlockCropTFC.Simple7(this);
            case 8:
                return new BlockCropTFC.Simple8(this);
        }
        throw new IllegalStateException("Invalid growthstage property " + growthStages + " for crop");
    }
}
