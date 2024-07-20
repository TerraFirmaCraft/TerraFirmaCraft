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
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Implementation of food mechanics, including decay (rot) through the creation date system, food traits (modifiers),
 * and nutrients. This must be provided by any food that can be eaten, as we overwrite vanilla food properties in our
 * overwrite of vanilla's food handler with {@link IPlayerInfo}, and overwrite edible food components in {@link TFCComponents}
 * <p>
 * Foods can be added via a {@link FoodDefinition} which is loaded via {@link FoodCapability#MANAGER}. A food component is
 * attached to every item stack, and updated on creation (constructor) if necessary.
 */
public interface IFood
{
    long ROTTEN_DATE = Long.MIN_VALUE;
    long NEVER_DECAY_DATE = Long.MAX_VALUE;

    long TRANSIENT_CREATION_DATE = -4;
    long ROTTEN_CREATION_DATE = -3;
    long NEVER_DECAY_CREATION_DATE = -2;
    long UNKNOWN_CREATION_DATE = -1;

    /**
     * The timestamp that this food was created, used to calculate expiration date. There are a few special meanings:
     * <ul>
     *     <li>{@link #TRANSIENT_CREATION_DATE} = The food is transiently non-decaying</li>
     *     <li>{@link #UNKNOWN_CREATION_DATE} = The food was created at an unknown time. This will be reset whenever possible.</li>
     *     <li>{@link #NEVER_DECAY_CREATION_DATE} = The food will never decay</li>
     *     <li>{@link #ROTTEN_CREATION_DATE} = The food is currently rotten</li>
     * </ul>
     *
     * @return The tick that this food was created.
     */
    long getCreationDate();

    /**
     * @return {@code true} if the food is rotten / decayed.
     */
    boolean isRotten();

    /**
     * @return The food data associated with this food, either custom or from the food definition
     */
    FoodData getData();

    /**
     * Gets the current decay date modifier, including traits
     * Note: there's a difference between the DECAY modifier, and the DECAY DATE modifier, in that they are reciprocals of each other
     *
     * @return a value between 0 and infinity (0 = instant decay, infinity = never decay)
     */
    default float getDecayDateModifier()
    {
        // Decay modifiers are higher = shorter
        float mod = getData().decayModifier() * Helpers.getValueOrDefault(TFCConfig.SERVER.foodDecayModifier).floatValue();
        for (FoodTrait trait : getTraits())
        {
            mod *= trait.getDecayModifier();
        }
        // The modifier returned is used to calculate time, so higher = longer
        return mod == 0 ? Float.POSITIVE_INFINITY : 1 / mod;
    }

    /**
     * Returns a list of all traits applied to the food. The traits present on the food <strong>cannot</strong> be mutated through this list!
     * <p>
     * Food traits can be applied via the methods on {@link FoodCapability} which safely preserve the remaining decay amount.
     *
     * @return A list of all traits applied to the food.
     * @see FoodCapability#applyTrait
     * @see FoodCapability#removeTrait
     */
    List<FoodTrait> getTraits();

    /**
     * @return {@code true} if this food has {@code trait}.
     */
    default boolean hasTrait(FoodTrait trait)
    {
        return getTraits().contains(trait);
    }

    /**
     * Tooltip added to the food item
     *
     * @param stack the stack in question
     * @param text  the tooltip
     */
    default void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        final long creationDate = getCreationDate();
        if (creationDate == ROTTEN_CREATION_DATE)
        {
            text.add(Component.translatable("tfc.tooltip.food_rotten").withStyle(ChatFormatting.RED));
            if (((stack.hashCode() * 1928634918231L) & 0xFF) == 0)
            {
                text.add(Component.translatable("tfc.tooltip.food_rotten_special").withStyle(ChatFormatting.RED));
            }
        }
        else if (creationDate == NEVER_DECAY_CREATION_DATE)
        {
            text.add(Component.translatable("tfc.tooltip.food_infinite_expiry").withStyle(ChatFormatting.GOLD));
        }
        else if (creationDate != TRANSIENT_CREATION_DATE) // Don't show anything for transient dates
        {
            final long rottenDate = FoodCapability.getRottenDate(creationDate, getDecayDateModifier());
            final long rottenCalendarTime = Calendars.CLIENT.ticksToCalendarTicks(rottenDate); // Date food rots on.
            final long ticksRemaining = rottenDate - Calendars.CLIENT.getTicks(); // Ticks remaining until rotten

            final MutableComponent tooltip = switch (TFCConfig.CLIENT.foodExpiryTooltipStyle.get())
            {
                case EXPIRY -> Component.translatable("tfc.tooltip.food_expiry_date", ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()));
                case TIME_LEFT -> Component.translatable("tfc.tooltip.food_expiry_left", Calendars.CLIENT.getTimeDelta(ticksRemaining));
                case BOTH -> Component.translatable("tfc.tooltip.food_expiry_date_and_left", ICalendar.getTimeAndDate(rottenCalendarTime, Calendars.CLIENT.getCalendarDaysInMonth()), Calendars.CLIENT.getTimeDelta(ticksRemaining));
                default -> null;
            };
            if (tooltip != null)
            {
                text.add(tooltip.withStyle(ChatFormatting.DARK_GREEN));
            }
        }

        // Nutrition / Hunger / Saturation / Water Values
        // Hide this based on the shift key (because it's a lot of into)
        if (ClientHelpers.hasShiftDown())
        {
            text.add(Component.translatable("tfc.tooltip.nutrition").withStyle(ChatFormatting.GRAY));

            boolean any = false;
            if (!isRotten())
            {
                final FoodData data = getData();

                float saturation = data.saturation();
                if (saturation > 0)
                {
                    // This display makes it so 100% saturation means a full hunger bar worth of saturation.
                    text.add(Component.translatable("tfc.tooltip.nutrition_saturation", String.format("%d", (int) (saturation * 5))).withStyle(ChatFormatting.GRAY));
                    any = true;
                }
                int water = (int) data.water();
                if (water > 0)
                {
                    text.add(Component.translatable("tfc.tooltip.nutrition_water", String.format("%d", water)).withStyle(ChatFormatting.GRAY));
                    any = true;
                }

                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    float value = data.nutrient(nutrient);
                    if (value > 0)
                    {
                        text.add(Component.literal(" - ")
                            .append(Helpers.translateEnum(nutrient))
                            .append(": " + String.format("%.1f", value))
                            .withStyle(nutrient.getColor()));
                        any = true;
                    }
                }
            }
            if (!any)
            {
                text.add(Component.translatable("tfc.tooltip.nutrition_none").withStyle(ChatFormatting.GRAY));
            }
        }
        else
        {
            text.add(Component.translatable("tfc.tooltip.hold_shift_for_nutrition_info").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }

        // Add info for each trait
        for (FoodTrait trait : getTraits())
        {
            trait.addTooltipInfo(text);
        }
    }
}
