/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Capability for any food item
 * Allows foods to have nutrients, and also to decay / rot.
 *
 * Important: A lot of these methods take a {@code isClientSide} boolean. This is in order for the internal handlers to query the right calendar instance for the current logical side. DO NOT just use the overloads that pass {@code false} in, especially if you are on client side!
 */
public interface IFood extends INetworkFood
{
    /**
     * The timestamp that this food was created, used to calculate expiration date.
     * There are a few special meanings:
     * - {@link FoodHandler#UNKNOWN_CREATION_DATE} = The food was created at an unknown time. This will be reset whenever possible.
     * - {@link FoodHandler#ROTTEN_DATE} = The food is currently rotten
     * - {@link FoodHandler#NEVER_DECAY_DATE} = The food will never decay
     *
     * @return the calendar time of creation
     */
    long getCreationDate();

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
    long getRottenDate();

    /**
     * @return {@code true} if the food is rotten / decayed.
     */
    default boolean isRotten()
    {
        return getRottenDate() < Calendars.get().getTicks();
    }

    /**
     * @return {@code true} if the food item is only transiently non-decaying / infinite expiry. Used to exclude displaying a tooltip.
     */
    default boolean isTransientNonDecaying()
    {
        return false;
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
    default void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        // Expiration dates
        if (isRotten())
        {
            text.add(Helpers.translatable("tfc.tooltip.food_rotten").withStyle(ChatFormatting.RED));
            if (((stack.hashCode() * 1928634918231L) & 0xFF) == 0)
            {
                text.add(Helpers.translatable("tfc.tooltip.food_rotten_special").withStyle(ChatFormatting.RED));
            }
        }
        else
        {
            final long rottenDate = getRottenDate();
            if (rottenDate == Long.MAX_VALUE)
            {
                if (!isTransientNonDecaying())
                {
                    text.add(Helpers.translatable("tfc.tooltip.food_infinite_expiry").withStyle(ChatFormatting.GOLD));
                }
            }
            else
            {
                final long rottenCalendarTime = Calendars.CLIENT.ticksToCalendarTicks(rottenDate); // Date food rots on.
                final long ticksRemaining = rottenDate - Calendars.CLIENT.getTicks(); // Ticks remaining until rotten

                final MutableComponent tooltip = switch (TFCConfig.CLIENT.foodExpiryTooltipStyle.get())
                    {
                        case EXPIRY -> Helpers.translatable("tfc.tooltip.food_expiry_date", ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()));
                        case TIME_LEFT -> Helpers.translatable("tfc.tooltip.food_expiry_left", Calendars.CLIENT.getTimeDelta(ticksRemaining));
                        case BOTH -> Helpers.translatable("tfc.tooltip.food_expiry_date_and_left", ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()), Calendars.CLIENT.getTimeDelta(ticksRemaining));
                        default -> null;
                    };
                if (tooltip != null)
                {
                    text.add(tooltip.withStyle(ChatFormatting.DARK_GREEN));
                }
            }
        }

        // Nutrition / Hunger / Saturation / Water Values
        // Hide this based on the shift key (because it's a lot of into)
        if (ClientHelpers.hasShiftDown())
        {
            text.add(Helpers.translatable("tfc.tooltip.nutrition").withStyle(ChatFormatting.GRAY));

            boolean any = false;
            if (!isRotten())
            {
                final FoodData data = getData();

                float saturation = data.saturation();
                if (saturation > 0)
                {
                    // This display makes it so 100% saturation means a full hunger bar worth of saturation.
                    text.add(Helpers.translatable("tfc.tooltip.nutrition_saturation", String.format("%d", (int) (saturation * 5))).withStyle(ChatFormatting.GRAY));
                    any = true;
                }
                int water = (int) data.water();
                if (water > 0)
                {
                    text.add(Helpers.translatable("tfc.tooltip.nutrition_water", String.format("%d", water)).withStyle(ChatFormatting.GRAY));
                    any = true;
                }

                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    float value = data.nutrient(nutrient);
                    if (value > 0)
                    {
                        text.add(Helpers.literal(" - ")
                            .append(Helpers.translateEnum(nutrient))
                            .append(": " + String.format("%.1f", value))
                            .withStyle(nutrient.getColor()));
                        any = true;
                    }
                }
            }
            if (!any)
            {
                text.add(Helpers.translatable("tfc.tooltip.nutrition_none").withStyle(ChatFormatting.GRAY));
            }
        }
        else
        {
            text.add(Helpers.translatable("tfc.tooltip.hold_shift_for_nutrition_info").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }

        // Add info for each trait
        for (FoodTrait trait : getTraits())
        {
            trait.addTooltipInfo(stack, text);
        }

        if (TFCConfig.CLIENT.enableDebug.get())
        {
            text.add(Helpers.literal(ChatFormatting.DARK_GRAY + "[Debug] Created at: " + getCreationDate() + " rots at: " + getRottenDate()));
        }
    }
}
