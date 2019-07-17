/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSimple;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSpreading;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.util.agriculture.Crop.CropType.*;

public enum Crop implements ICrop
{
    BARLEY(Food.BARLEY, 5f, 35f, 100f, 400f, 8, 0.5f, SIMPLE),
    MAIZE(Food.MAIZE, 5f, 35f, 100f, 400f, 6, 0.5f, SIMPLE),
    OAT(Food.OAT, 5f, 35f, 100f, 400f, 8, 0.5f, SIMPLE),
    RICE(Food.RICE, 5f, 35f, 100f, 400f, 8, 0.5f, SIMPLE),
    RYE(Food.RYE, 5f, 35f, 100f, 400f, 8, 0.5f, SIMPLE),
    WHEAT(Food.WHEAT, 5f, 35f, 100f, 400f, 8, 0.5f, SIMPLE),
    BEET(Food.BEET, 5f, 35f, 100f, 400f, 7, 0.5f, SIMPLE),
    CABBAGE(Food.CABBAGE, 5f, 35f, 100f, 400f, 6, 0.5f, SIMPLE),
    CARROT(Food.CARROT, 5f, 35f, 100f, 400f, 5, 0.5f, SIMPLE),
    GARLIC(Food.GARLIC, 5f, 35f, 100f, 400f, 5, 0.5f, SIMPLE),
    GREEN_BEAN(Food.GREEN_BEAN, 5f, 35f, 100f, 400f, 7, 0.5f, SIMPLE),
    ONION(Food.ONION, 5f, 35f, 100f, 400f, 7, 0.5f, SIMPLE),
    POTATO(Food.POTATO, 5f, 35f, 100f, 400f, 7, 0.5f, SIMPLE),
    SOYBEAN(Food.SOYBEAN, 5f, 35f, 100f, 400f, 7, 0.5f, SIMPLE),
    SQUASH(Food.SQUASH, 5f, 35f, 100f, 400f, 8, 0.5f, SPREADING),
    SUGARCANE(Food.SUGARCANE, 5f, 35f, 100f, 400f, 8, 0.5f, SIMPLE),
    RED_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.RED_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)), 5f, 35f, 100f, 400f, 7, 0.5f, SIMPLE),
    TOMATO(Food.TOMATO, 5f, 35f, 100f, 400f, 8, 0.5f, SIMPLE),
    YELLOW_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.YELLOW_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)), 5f, 35f, 100f, 400f, 7, 0.5f, SIMPLE),
    JUTE(() -> new ItemStack(ItemsTFC.JUTE), () -> ItemStack.EMPTY, 5f, 35f, 100f, 400f, 6, 0.5f, SIMPLE),
    PUMPKIN(() -> new ItemStack(Blocks.PUMPKIN), () -> ItemStack.EMPTY, 5f, 35f, 100f, 400f, 8, 0.5f, SPREADING),
    MELON(() -> new ItemStack(Blocks.MELON_BLOCK), () -> ItemStack.EMPTY, 5f, 35f, 100f, 400f, 8, 0.5f, SPREADING);

    public static final PropertyInteger STAGE_8 = PropertyInteger.create("stage", 0, 7);
    public static final PropertyInteger STAGE_7 = PropertyInteger.create("stage", 0, 6);
    public static final PropertyInteger STAGE_6 = PropertyInteger.create("stage", 0, 5);
    public static final PropertyInteger STAGE_5 = PropertyInteger.create("stage", 0, 4);

    private final Supplier<ItemStack> foodDrop;
    private final Supplier<ItemStack> foodDropEarly;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final int growthStages;
    private final float growthTime;
    private final CropType type;

    Crop(Food foodDrop, float minTemp, float maxTemp, float minRain, float maxRain, int growthStages, float growthTime, CropType type)
    {
        this(() -> new ItemStack(ItemFoodTFC.get(foodDrop)), () -> ItemStack.EMPTY, minTemp, maxTemp, minRain, maxRain, growthStages, growthTime, type);
    }

    Crop(Supplier<ItemStack> foodDrop, Supplier<ItemStack> foodDropEarly, float minTemp, float maxTemp, float minRain, float maxRain, int growthStages, float growthTime, CropType type)
    {
        this.foodDrop = foodDrop;
        this.foodDropEarly = foodDropEarly;

        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;

        this.growthStages = growthStages;
        // The value stored it measured in hours, the input value is in months
        // todo: the input should be in days instead of months
        this.growthTime = growthTime * ICalendar.HOURS_IN_DAY * CalendarTFC.INSTANCE.getDaysInMonth();

        this.type = type;
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
        if (type == SIMPLE || type == PICKABLE)
        {
            switch (growthStages)
            {
                case 5:
                    return new BlockCropSimple(this, type == PICKABLE)
                    {
                        @Override
                        public PropertyInteger getStageProperty()
                        {
                            return STAGE_5;
                        }
                    };
                case 6:
                    return new BlockCropSimple(this, type == PICKABLE)
                    {
                        @Override
                        public PropertyInteger getStageProperty()
                        {
                            return STAGE_6;
                        }
                    };
                case 7:
                    return new BlockCropSimple(this, type == PICKABLE)
                    {
                        @Override
                        public PropertyInteger getStageProperty()
                        {
                            return STAGE_7;
                        }
                    };
                case 8:
                    return new BlockCropSimple(this, type == PICKABLE)
                    {
                        @Override
                        public PropertyInteger getStageProperty()
                        {
                            return STAGE_8;
                        }
                    };
            }
        }
        else if (type == SPREADING)
        {
            return new BlockCropSpreading(this);
        }
        throw new IllegalStateException("Invalid growthstage property " + growthStages + " for crop");
    }

    enum CropType
    {
        SIMPLE, PICKABLE, SPREADING
    }
}
