/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import java.util.function.Supplier;
import javax.annotation.Nonnull;

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
    // todo: unique rain tolerances for each crop
    BARLEY(Food.BARLEY,
        new Range(0, 4, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    MAIZE(Food.MAIZE,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        6, 0.5f, SIMPLE),
    OAT(Food.OAT,
        new Range(0, 4, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    RICE(Food.RICE,
        new Range(0, 4, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    RYE(Food.RYE,
        new Range(0, 4, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    WHEAT(Food.WHEAT,
        new Range(0, 4, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    BEET(Food.BEET,
        new Range(0, 5, 35, 40), // todo: unique temp range for beets
        new Range(100, 400, 50),
        7, 0.5f, SIMPLE),
    CABBAGE(Food.CABBAGE,
        new Range(0, 10, 35, 40),
        new Range(100, 400, 50),
        6, 0.5f, SIMPLE),
    CARROT(Food.CARROT,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        5, 0.5f, SIMPLE),
    GARLIC(Food.GARLIC,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        5, 0.5f, SIMPLE),
    GREEN_BEAN(Food.GREEN_BEAN,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        7, 0.5f, SIMPLE),
    ONION(Food.ONION,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        7, 0.5f, SIMPLE),
    POTATO(Food.POTATO,
        new Range(0, 4, 35, 40),
        new Range(100, 400, 50),
        7, 0.5f, SIMPLE),
    SOYBEAN(Food.SOYBEAN,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        7, 0.5f, SIMPLE),
    SQUASH(Food.SQUASH,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    SUGARCANE(Food.SUGARCANE,
        new Range(12, 18, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    TOMATO(Food.TOMATO,
        new Range(0, 8, 35, 40),
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    RED_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.RED_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)),
        new Range(4, 12, 35, 40),
        new Range(100, 400, 50),
        7, 0.5f, SIMPLE),
    YELLOW_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.YELLOW_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)),
        new Range(4, 12, 35, 40),
        new Range(100, 400, 50),
        7, 0.5f, SIMPLE),
    JUTE(() -> new ItemStack(ItemsTFC.JUTE), () -> ItemStack.EMPTY,
        new Range(5, 10, 35, 40),
        new Range(100, 400, 50),
        6, 0.5f, SIMPLE),
    PUMPKIN(() -> new ItemStack(Blocks.PUMPKIN), () -> ItemStack.EMPTY,
        new Range(0, 5, 35, 40), // todo: unique temp range for pumpkins
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE),
    MELON(() -> new ItemStack(Blocks.MELON_BLOCK), () -> ItemStack.EMPTY,
        new Range(0, 5, 35, 40),  // todo: unique temp range for melons
        new Range(100, 400, 50),
        8, 0.5f, SIMPLE);

    private final Supplier<ItemStack> foodDrop;
    private final Supplier<ItemStack> foodDropEarly;
    private final Range tempRange;
    private final Range rainRange;
    private final int growthStages;
    private final float growthTime;
    private final CropType type;

    Crop(Food foodDrop, Range tempRange, Range rainRange, int growthStages, float growthTime, CropType type)
    {
        this(() -> new ItemStack(ItemFoodTFC.get(foodDrop)), () -> ItemStack.EMPTY, tempRange, rainRange, growthStages, growthTime, type);
    }

    Crop(Supplier<ItemStack> foodDrop, Supplier<ItemStack> foodDropEarly, Range tempRange, Range rainRange, int growthStages, float growthTime, CropType type)
    {
        this.foodDrop = foodDrop;
        this.foodDropEarly = foodDropEarly;

        this.tempRange = tempRange;
        this.rainRange = rainRange;

        this.growthStages = growthStages;
        this.growthTime = growthTime; // This is measured in % of months

        this.type = type;
    }

    @Override
    public float getGrowthTime()
    {
        return growthTime * CalendarTFC.CALENDAR_TIME.getDaysInMonth() * ICalendar.TICKS_IN_DAY;
    }

    @Override
    public int getMaxStage()
    {
        return growthStages - 1;
    }

    @Override
    public boolean isValidConditions(float temperature, float rainfall)
    {
        return tempRange.minAlive < temperature && temperature < tempRange.maxAlive && rainRange.minAlive < rainfall && rainfall < rainRange.maxAlive;
    }

    @Override
    public boolean isValidForGrowth(float temperature, float rainfall)
    {
        return tempRange.minGrow < temperature && temperature < tempRange.maxGrow && rainRange.minGrow < rainfall && rainfall < rainRange.maxGrow;
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
            return BlockCropSimple.create(this, type == PICKABLE);
        }
        else if (type == SPREADING)
        {
            return BlockCropSpreading.create(this);
        }
        throw new IllegalStateException("Invalid growthstage property " + growthStages + " for crop");
    }

    enum CropType
    {
        SIMPLE, PICKABLE, SPREADING
    }

    private static class Range
    {
        public final float minAlive, maxAlive, minGrow, maxGrow;

        public Range(float minGrow, float maxGrow, float tolerance)
        {
            this(minGrow - tolerance, minGrow, maxGrow, maxGrow + tolerance);
        }

        public Range(float minAlive, float minGrow, float maxGrow, float maxAlive)
        {
            this.minAlive = minAlive;
            this.minGrow = minGrow;
            this.maxGrow = maxGrow;
            this.maxAlive = maxAlive;
        }
    }
}
