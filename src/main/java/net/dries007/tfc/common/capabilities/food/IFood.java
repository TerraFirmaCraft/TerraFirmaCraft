/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

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
public interface IFood extends INBTSerializable<CompoundTag>
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
     * @return true if the food is rotten / decayed.
     */
    default boolean isRotten()
    {
        return getRottenDate() < Calendars.get().getTicks();
    }

    /**
     * Get a visible measure of all immutable data associated with food
     * - Nutrition information
     * - Hunger / Saturation
     * - Water (Thirst)
     *
     * @see FoodRecord
     */
    FoodRecord getData();

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
            text.add(new TranslatableComponent("tfc.tooltip.food_rotten").withStyle(ChatFormatting.RED));
            if (((stack.hashCode() * 1928634918231L) & 0xFF) == 0)
            {
                text.add(new TranslatableComponent("tfc.tooltip.food_rotten_special").withStyle(ChatFormatting.RED));
            }
        }
        else
        {
            long rottenDate = getRottenDate();
            if (rottenDate == Long.MAX_VALUE)
            {
                text.add(new TranslatableComponent("tfc.tooltip.food_infinite_expiry").withStyle(ChatFormatting.GOLD));
            }
            else
            {
                final long rottenCalendarTime = Calendars.CLIENT.ticksToCalendarTicks(rottenDate); // Date food rots on.
                final long ticksRemaining = rottenDate - Calendars.CLIENT.getTicks(); // Ticks remaining until rotten

                final Component tooltip = switch (TFCConfig.CLIENT.foodExpiryTooltipStyle.get())
                    {
                        case EXPIRY -> new TranslatableComponent("tfc.tooltip.food_expiry_date")
                            .append(ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()))
                            .withStyle(ChatFormatting.DARK_GREEN);
                        case TIME_LEFT -> new TranslatableComponent("tfc.tooltip.food_expiry_left")
                            .append(Calendars.CLIENT.getTimeDelta(ticksRemaining))
                            .withStyle(ChatFormatting.DARK_GREEN);
                        case BOTH -> new TranslatableComponent("tfc.tooltip.food_expiry_date")
                            .append(ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()))
                            .append(new TranslatableComponent("tfc.tooltip.food_expiry_and_days_left"))
                            .append(Calendars.CLIENT.getTimeDelta(ticksRemaining))
                            .append(new TranslatableComponent("tfc.tooltip.food_expiry_and_days_left_close"))
                            .withStyle(ChatFormatting.DARK_GREEN);
                        default -> null;
                    };
                if (tooltip != null)
                {
                    text.add(tooltip);
                }
            }
        }

        // Nutrition / Hunger / Saturation / Water Values
        // Hide this based on the shift key (because it's a lot of into)
        if (ClientHelpers.hasShiftDown())
        {
            text.add(new TranslatableComponent("tfc.tooltip.nutrition").withStyle(ChatFormatting.GRAY));

            boolean any = false;
            if (!isRotten())
            {
                final FoodRecord data = getData();

                float saturation = data.getSaturation();
                if (saturation > 0)
                {
                    // This display makes it so 100% saturation means a full hunger bar worth of saturation.
                    text.add(new TranslatableComponent("tfc.tooltip.nutrition_saturation", String.format("%d", (int) (saturation * 5))).withStyle(ChatFormatting.GRAY));
                    any = true;
                }
                float water = data.getWater();
                if (water > 0)
                {
                    text.add(new TranslatableComponent("tfc.tooltip.nutrition_water", String.format("%d", (int) water)).withStyle(ChatFormatting.GRAY));
                    any = true;
                }

                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    float value = data.getNutrient(nutrient);
                    if (value > 0)
                    {
                        text.add(new TextComponent(" - ")
                            .append(Helpers.translateEnum(nutrient))
                            .append(": " + String.format("%.1f", value))
                            .withStyle(nutrient.getColor()));
                        any = true;
                    }
                }
            }
            if (!any)
            {
                text.add(new TranslatableComponent("tfc.tooltip.nutrition_none").withStyle(ChatFormatting.GRAY));
            }
        }
        else
        {
            text.add(new TranslatableComponent("tfc.tooltip.hold_shift_for_nutrition_info").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }

        // Add info for each trait
        for (FoodTrait trait : getTraits())
        {
            trait.addTooltipInfo(stack, text);
        }

        if (TFCConfig.CLIENT.enableDebug.get())
        {
            text.add(new TextComponent(ChatFormatting.DARK_GRAY + "[Debug] Created at: " + getCreationDate() + " rots at: " + getRottenDate()));
        }
    }
}
