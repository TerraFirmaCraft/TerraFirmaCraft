/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.IBerryBush;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;

public enum BerryBush implements IBerryBush
{
	BLACKBERRY(Food.BLACKBERRY, Month.MAY, 4, 5f, 35f, 100f, 400f, 0.5f);

	@Nullable
	public static BerryBush getFromFruit(Food fruit)
	{
		for (BerryBush bush : BerryBush.values())
		{
			if (bush.getFruit().equals(fruit))
				return bush;
		}
		return null;
	}

	private final Food fruit;
	private final Month harvestMonthStart;
	private final int harvestingMonths;
	private final float growthTime;
	private final float minTemp;
	private final float maxTemp;
	private final float minRain;
	private final float maxRain;

	BerryBush(Food fruit, Month harvestMonthStart, int harvestingMonths, float minTemp, float maxTemp, float minRain, float maxRain, float growthTime)
	{
		this.fruit = fruit;
		this.harvestMonthStart = harvestMonthStart;
		this.harvestingMonths = harvestingMonths;
		this.growthTime = growthTime * CalendarTFC.INSTANCE.getDaysInMonth() * ICalendar.HOURS_IN_DAY;

		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.minRain = minRain;
		this.maxRain = maxRain;
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
			if (testing.equals(month))
				return true;
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
		return new ItemStack(ItemFoodTFC.get(this.getFruit()));
	}
}
