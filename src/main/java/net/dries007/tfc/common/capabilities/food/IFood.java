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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Implementation of food mechanics, including decay (rot) through the creation date system, food traits (modifiers),
 * and nutrients. This must be provided by any food that can be eaten, as we ignore vanilla food properties in our
 * overwrite of vanilla's food handler with {@link TFCFoodData}.
 * <p>
 * Foods can be added via a {@link FoodDefinition} which is loaded via JSON. TFC then attaches a {@link FoodHandler} as
 * a capability to each food as required.
 */
public interface IFood extends INBTSerializable<CompoundTag>
{
    /**
     * The timestamp that this food was created, used to calculate expiration date. There are a few special meanings:
     * <ul>
     *     <li>{@link FoodHandler#UNKNOWN_CREATION_DATE} = The food was created at an unknown time. This will be reset whenever possible.</li>
     *     <li>{@link FoodHandler#NEVER_DECAY_CREATION_DATE} = The food will never decay</li>
     *     <li>{@link FoodHandler#ROTTEN_CREATION_DATE} = The food is currently rotten</li>
     * </ul>
     *
     * @return The tick that this food was created.
     */
    long getCreationDate();

    /**
     * Sets the creation date. DO NOT USE TO PRESERVE FOOD! Use {@link FoodTrait} instead
     *
     * @param creationDate A tick.
     */
    void setCreationDate(long creationDate);

    /**
     * Get the date at which this food item will rot. There are a few special meanings:
     * <ul>
     *     <li>{@link FoodHandler#ROTTEN_DATE} = The food is currently rotten</li>
     *     <li>{@link FoodHandler#NEVER_DECAY_DATE} = The food will never decay</li>
     * </ul>
     *
     * @return The tick that this food will rot.
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
     * Returns a list of all traits applied to the food. The traits present on the food <strong>can be mutated</strong> through this list.
     * <p>
     * In general, when applying or removing traits for the purpose of preservation, prefer using the methods in {@link FoodCapability}, as
     * they will account for updating the creation date accordingly in order to prevent the food from becoming rotten.
     *
     * @return A list of all traits applied to the food.
     *
     * @see #hasTrait(FoodTrait)
     * @see FoodCapability#applyTrait(IFood, FoodTrait)
     * @see FoodCapability#removeTrait(IFood, FoodTrait)
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
        // Expiration dates
        if (isRotten())
        {
            text.add(Component.translatable("tfc.tooltip.food_rotten").withStyle(ChatFormatting.RED));
            if (((stack.hashCode() * 1928634918231L) & 0xFF) == 0)
            {
                text.add(Component.translatable("tfc.tooltip.food_rotten_special").withStyle(ChatFormatting.RED));
            }
        }
        else
        {
            final long rottenDate = getRottenDate();
            if (rottenDate == FoodHandler.NEVER_DECAY_DATE)
            {
                if (!isTransientNonDecaying())
                {
                    text.add(Component.translatable("tfc.tooltip.food_infinite_expiry").withStyle(ChatFormatting.GOLD));
                }
            }
            else
            {
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
            trait.addTooltipInfo(stack, text);
        }

        if (TFCConfig.CLIENT.enableDebug.get())
        {
            text.add(Component.literal(ChatFormatting.DARK_GRAY + "[Debug] Created at: " + getCreationDate() + " rots at: " + getRottenDate()));
        }
    }
}
