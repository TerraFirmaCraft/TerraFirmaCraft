/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.IBerryBush;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.classic.worldgen.WorldGenBerryBushes;

import static net.dries007.tfc.api.types.IBerryBush.Size.*;

public enum BerryBush implements IBerryBush
{
    BLACKBERRY(Food.BLACKBERRY, Month.MAY, 4, 7f, 20f, 100f, 400f, 0.8f, LARGE, true),
    BLUEBERRY(Food.BLUEBERRY, Month.JUNE, 3, 7f, 25f, 100f, 400f, 0.8f, LARGE, false),
    BUNCH_BERRY(Food.BUNCH_BERRY, Month.JUNE, 3, 15f, 30f, 100f, 400f, 0.8f, SMALL, false),
    CLOUD_BERRY(Food.CLOUD_BERRY, Month.JUNE, 2, 3f, 17f, 100f, 400f, 0.8f, MEDIUM, false),
    CRANBERRY(Food.CRANBERRY, Month.AUGUST, 3, 1f, 19f, 100f, 400f, 0.8f, MEDIUM, false),
    ELDERBERRY(Food.ELDERBERRY, Month.JULY, 2, 10f, 29f, 100f, 400f, 0.8f, LARGE, false),
    GOOSEBERRY(Food.GOOSEBERRY, Month.MARCH, 4, 5f, 27f, 100f, 400f, 0.8f, MEDIUM, false),
    RASPBERRY(Food.RASPBERRY, Month.JUNE, 2, 5f, 20f, 100f, 400f, 0.8f, LARGE, true),
    SNOW_BERRY(Food.SNOW_BERRY, Month.JULY, 2, -5f, 18f, 100f, 400f, 0.8f, SMALL, false),
    STRAWBERRY(Food.STRAWBERRY, Month.MARCH, 3, 5f, 28f, 100f, 400f, 0.8f, SMALL, false),
    WINTERGREEN_BERRY(Food.WINTERGREEN_BERRY, Month.AUGUST, 2, -5f, 17f, 100f, 400f, 0.8f, SMALL, false);

    static
    {
        for (IBerryBush bush : values())
        {
            WorldGenBerryBushes.register(bush);
        }
    }

    private final Food fruit;
    private final Month harvestMonthStart;
    private final int harvestingMonths;
    private final float growthTime;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final Size size;
    private final boolean hasSpikes;

    BerryBush(Food fruit, Month harvestMonthStart, int harvestingMonths, float minTemp, float maxTemp, float minRain, float maxRain, float growthTime, Size size, boolean spiky)
    {
        this.fruit = fruit;
        this.harvestMonthStart = harvestMonthStart;
        this.harvestingMonths = harvestingMonths;
        this.growthTime = growthTime * CalendarTFC.CALENDAR_TIME.getDaysInMonth() * ICalendar.HOURS_IN_DAY;

        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;

        this.size = size;
        this.hasSpikes = spiky;
    }

    public Food getFruit()
    {
        return this.fruit;
    }

    @Override
    public float getGrowthTime()
    {
        return this.growthTime;
    }

    @Override
    public boolean isHarvestMonth(Month month)
    {
        Month testing = this.harvestMonthStart;
        for (int i = 0; i < this.harvestingMonths; i++)
        {
            if (testing.equals(month)) return true;
            testing = testing.next();
        }
        return false;
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
    public ItemStack getFoodDrop()
    {
        return new ItemStack(ItemFoodTFC.get(getFruit()));
    }

    public Size getSize() { return this.size; }

    @Override
    public boolean isSpiky()
    {
        return hasSpikes;
    }
}
