/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
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
    /**
     * This flag indicates the food is intended to be transiently non-decaying. It does not preserve across {@link ItemStack#copy()} operations,
     * it will never expire, and it does not show any "Never Expires" tooltip.
     * <p>
     * If the food value (including decay modifier) is not present, the food item will have a default infinite expiry time - this will take
     * precedence and override the tooltip display.
     */
    long TRANSIENT_NEVER_DECAY_FLAG = -1;

    /**
     * This flag indicates the food is intended to be non-decaying, but that behavior should be invisible to the user, effectively
     * disabling decay for this item. Food with this creation date will never expire, and it will not show any "Never Expires" tooltip.
     */
    long INVISIBLE_NEVER_DECAY_FLAG = -2;

    /**
     * This flag indicates the food is intended to be non-decaying. It will never expire, and will show a "Never Expires" tooltip.
     * Note that foods may also be considered "Never Decaying" if the preservation returns {@link Float#POSITIVE_INFINITY}, despite
     * not having this creation date.
     */
    long NEVER_DECAY_FLAG = -3;

    /**
     * This flag indicates the food is rotten. It will show the "Rotten" tooltip.
     */
    long ROTTEN_FLAG = -4;

    /**
     * The timestamp that this food was created, used to calculate expiration date. Negative values may be one of the above flags,
     * which have special meanings as documented above.
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
     * Gets the current decay date modifier, including traits.
     * Note: there's a difference between the DECAY modifier, and the DECAY DATE modifier, in that they are reciprocals of each other
     *
     * @return a value between 0 and infinity (0 = instant decay, infinity = never decay)
     */
    default float getDecayDateModifier()
    {
        // Decay modifiers are higher = shorter
        float mod = getData().decayModifier() * TFCConfig.SERVER.foodDecayModifier.get().floatValue();
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
     * @return {@code true} if this food has {@code trait}.
     */
    default boolean hasTrait(Holder<FoodTrait> trait)
    {
        return hasTrait(trait.value());
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
        if (creationDate == ROTTEN_FLAG)
        {
            text.add(Component.translatable("tfc.tooltip.food_rotten").withStyle(ChatFormatting.RED));
            if (((stack.hashCode() * 1928634918231L) & 0xFF) == 0)
            {
                text.add(Component.translatable("tfc.tooltip.food_rotten_special").withStyle(ChatFormatting.RED));
            }
        }
        else if (
            creationDate == NEVER_DECAY_FLAG ||
            (creationDate != TRANSIENT_NEVER_DECAY_FLAG && getDecayDateModifier() == Float.POSITIVE_INFINITY))
        {
            text.add(Component.translatable("tfc.tooltip.food_infinite_expiry").withStyle(ChatFormatting.GOLD));
        }
        else if (
            // Both of these flags don't show anything, so don't add expiry tooltips
            creationDate != TRANSIENT_NEVER_DECAY_FLAG &&
            creationDate != INVISIBLE_NEVER_DECAY_FLAG)
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

        // Nutrition / Water / Saturation / Hunger Values
        // This is, by default, not shown unless shift is held down - instead, a "Hold shift for Nutrition" is shown
        // However, if the food is rotten, or if there is no nutrition, no tooltip is shown at all
        if (creationDate != ROTTEN_FLAG) // Only show if not rotten
        {
            // Calculate the tooltip to show first
            final List<Component> nutritionText = new ArrayList<>();
            final FoodData data = getData();

            float saturation = data.saturation();
            if (saturation > 0)
            {
                // This display makes it so 100% saturation means a full hunger bar worth of saturation.
                nutritionText.add(Component.translatable("tfc.tooltip.nutrition_saturation", String.format("%d", (int) (saturation * 5))).withStyle(ChatFormatting.GRAY));
            }
            int water = (int) data.water();
            if (water > 0)
            {
                nutritionText.add(Component.translatable("tfc.tooltip.nutrition_water", String.format("%d", water)).withStyle(ChatFormatting.GRAY));
            }

            for (Nutrient nutrient : Nutrient.VALUES)
            {
                float value = data.nutrient(nutrient);
                if (value > 0)
                {
                    nutritionText.add(Component.literal(" - ")
                        .append(Helpers.translateEnum(nutrient))
                        .append(": " + String.format("%.1f", value))
                        .withStyle(nutrient.getColor()));
                }
            }

            // If there is no nutrition tooltips, don't show anything
            if (!nutritionText.isEmpty())
            {
                // Otherwise, if we have shift down, include the full tooltip, if not, just the "Hold shift for more"
                if (ClientHelpers.hasShiftDown())
                {
                    text.add(Component.translatable("tfc.tooltip.nutrition").withStyle(ChatFormatting.GRAY));
                    text.addAll(nutritionText);
                }
                else
                {
                    text.add(Component.translatable("tfc.tooltip.hold_shift_for_nutrition_info").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
                }
            }
        }

        // Finally, show each food trait that has been applied
        for (FoodTrait trait : getTraits())
        {
            trait.addTooltipInfo(text);
        }
    }
}
