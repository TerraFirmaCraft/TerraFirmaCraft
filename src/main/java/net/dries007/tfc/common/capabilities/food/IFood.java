/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Capability for any food item
 * Allows foods to have nutrients, and also to decay / rot
 */
public interface IFood extends INBTSerializable<CompoundNBT>
{
    /**
     * The timestamp that this food was created
     * Used to calculate expiration date
     * Rotten food uses {@code Long.MIN_VALUE} as the creation date
     *
     * @return the calendar time of creation
     */
    default long getCreationDate()
    {
        return getCreationDate(false);
    }

    long getCreationDate(boolean isClientSide);

    /**
     * Sets the creation date. DO NOT USE TO PRESERVE FOOD! Use {@link FoodTrait} instead
     *
     * @param creationDate A calendar time
     */
    void setCreationDate(long creationDate);

    /**
     * Get the date at which this food item will rot
     *
     * @return a calendar time
     */
    default long getRottenDate()
    {
        return getRottenDate(false);
    }

    long getRottenDate(boolean isClientSide);

    /**
     * @return true if the food is rotten / decayed.
     */
    default boolean isRotten()
    {
        return isRotten(false);
    }

    default boolean isRotten(boolean isClientSide)
    {
        return getRottenDate(isClientSide) < Calendars.get(isClientSide).getTicks();
    }

    /**
     * Get a visible measure of all immutable data associated with food
     * - Nutrition information
     * - Hunger / Saturation
     * - Water (Thirst)
     *
     * @see FoodData
     */
    FoodData getData();

    /**
     * Gets the current decay date modifier, including traits
     * Note: there's a difference between the DECAY modifier, and the DECAY DATE modifier, in that they are reciprocals of each other
     *
     * @return a value between 0 and infinity (0 = instant decay, infinity = never decay)
     */
    float getDecayDateModifier();

    /**
     * If the item is a food capability item, and it was created before the post init, we assume that it is a technical stack, and will not appear in the world without a copy. As such, we set it to non-decaying.
     * This is NOT SERIALIZED on the capability - as a result it will not persist across {@link ItemStack#copy()},
     */
    void setNonDecaying();

    /**
     * Gets the current list of traits on this food
     * Can also be used to add traits to the food
     *
     * @return the traits of the food
     */
    List<FoodTrait> getTraits();

    /**
     * Tooltip added to the food item
     *
     * @param stack the stack in question
     * @param text  the tooltip
     */
    default void addTooltipInfo(ItemStack stack, List<ITextComponent> text)
    {
        // Expiration dates
        if (isRotten(true))
        {
            text.add(new TranslationTextComponent("tfc.tooltip.food_rotten").withStyle(TextFormatting.RED));
            if (((stack.hashCode() * 1928634918231L) & 0xFF) == 0)
            {
                text.add(new TranslationTextComponent("tfc.tooltip.food_rotten_special").withStyle(TextFormatting.RED));
            }
        }
        else
        {
            long rottenDate = getRottenDate(true);
            if (rottenDate == Long.MAX_VALUE)
            {
                text.add(new TranslationTextComponent("tfc.tooltip.food_infinite_expiry").withStyle(TextFormatting.GOLD));
            }
            else
            {
                final long rottenCalendarTime = rottenDate - Calendars.CLIENT.getTicks() + Calendars.CLIENT.getCalendarTicks(); // Date food rots on.
                final long daysToRotInTicks = ICalendar.getTotalDays(rottenCalendarTime - Calendars.CLIENT.getCalendarTicks()); // Days till food rots.

                switch (TFCConfig.CLIENT.foodExpiryTooltipStyle.get())
                {
                    case EXPIRY:
                        text.add(new TranslationTextComponent("tfc.tooltip.food_expiry_date")
                            .append(ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()))
                            .withStyle(TextFormatting.DARK_GREEN));
                        break;
                    case TIME_LEFT:
                        if (daysToRotInTicks < 1)
                        {
                            text.add(new TranslationTextComponent("tfc.tooltip.food_expiry_less_than_one_day_left")
                                .withStyle(TextFormatting.DARK_GREEN));
                        }
                        else
                        {
                            text.add(new TranslationTextComponent("tfc.tooltip.food_expiry_days_left", String.valueOf(daysToRotInTicks))
                                .withStyle(TextFormatting.DARK_GREEN));
                        }
                        break;
                    case BOTH:
                        final ITextComponent timeLeft;
                        if (daysToRotInTicks < 1)
                        {
                            timeLeft = new TranslationTextComponent("tfc.tooltip.food_expiry_and_less_than_one_day_left");
                        }
                        else
                        {
                            timeLeft = new TranslationTextComponent("tfc.tooltip.food_expiry_and_days_left", String.valueOf(daysToRotInTicks));
                        }
                        text.add(new TranslationTextComponent("tfc.tooltip.food_expiry_date")
                            .append(ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()))
                            .append(timeLeft)
                            .withStyle(TextFormatting.DARK_GREEN));
                        break;
                }
            }
        }

        // Nutrition / Hunger / Saturation / Water Values
        // Hide this based on the shift key (because it's a lot of into)
        if (ClientHelpers.hasShiftDown())
        {
            text.add(new TranslationTextComponent("tfc.tooltip.nutrition").withStyle(TextFormatting.GRAY));

            boolean any = false;
            if (!isRotten(true))
            {
                final FoodData data = getData();

                float saturation = data.getSaturation();
                if (saturation > 0)
                {
                    // This display makes it so 100% saturation means a full hunger bar worth of saturation.
                    text.add(new TranslationTextComponent("tfc.tooltip.nutrition_saturation", String.format("%d", (int) (saturation * 5))).withStyle(TextFormatting.GRAY));
                    any = true;
                }
                float water = data.getWater();
                if (water > 0)
                {
                    text.add(new TranslationTextComponent("tfc.tooltip.nutrition_water", String.format("%d", (int) water)).withStyle(TextFormatting.GRAY));
                    any = true;
                }

                final float[] nutrients = data.getNutrients();
                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    float value = nutrients[nutrient.ordinal()];
                    if (value > 0)
                    {
                        text.add(new StringTextComponent(" - ")
                            .append(new TranslationTextComponent(Helpers.getEnumTranslationKey(nutrient)))
                            .append(": " + String.format("%.1f", value))
                            .withStyle(nutrient.getColor()));
                        any = true;
                    }
                }
            }
            if (!any)
            {
                text.add(new TranslationTextComponent("tfc.tooltip.nutrition_none").withStyle(TextFormatting.GRAY));
            }
        }
        else
        {
            text.add(new TranslationTextComponent("tfc.tooltip.hold_shift_for_nutrition_info").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
        }

        // Add info for each trait
        for (FoodTrait trait : getTraits())
        {
            trait.addTraitInfo(stack, text);
        }

        if (TFCConfig.CLIENT.enableDebug.get())
        {
            text.add(new StringTextComponent("[Debug] Created at: " + getCreationDate() + " rots at: " + getRottenDate()));
        }
    }
}
