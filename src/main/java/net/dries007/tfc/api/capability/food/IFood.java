/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarFormatted;

/**
 * Capability for any food item
 * Allows foods to have nutrients, and also to decay / rot
 */
public interface IFood extends INBTSerializable<NBTTagCompound>
{
    /**
     * The timestamp that this food was created
     * Used to calculate expiration date
     * Rotten food uses {@code Long.MIN_VALUE} as the creation date
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
        return getRottenDate() < CalendarTFC.PLAYER_TIME.getTicks();
    }

    /**
     * Get a visible measure of all immutable data associated with food
     * - Nutrition information
     * - Hunger / Saturation
     * - Water (Thirst)
     *
     * @see FoodData
     */
    @Nonnull
    FoodData getData();

    /**
     * Gets the current decay date modifier, including traits
     * Note: there's a difference between the DECAY modifier, and the DECAY DATE modifier, in that they are reciprocals of eachother
     *
     * @return a value between 0 and infinity (0 = instant decay, infinity = never decay)
     */
    float getDecayDateModifier();

    /**
     * Called from {@link net.dries007.tfc.CommonEventHandler#attachItemCapabilities(AttachCapabilitiesEvent)}
     * If the item is a food capability item, and it was created before the post init, we assume that it is a technical stack, and will not appear in the world without a copy. As such, we set it to non-decaying.
     * This is NOT SERIALIZED on the capability - as a result it will not persist across {@link ItemStack#copy()},
     * See TerraFirmaCraft#458
     */
    void setNonDecaying();

    /**
     * Gets the current list of traits on this food
     * Can also be used to add traits to the food
     *
     * @return the traits of the food
     */
    @Nonnull
    List<FoodTrait> getTraits();

    /**
     * Tooltip added to the food item
     * Called from {@link net.dries007.tfc.client.ClientEvents}
     *
     * @param stack the stack in question
     * @param text  the tooltip
     */
    @SideOnly(Side.CLIENT)
    default void addTooltipInfo(@Nonnull ItemStack stack, @Nonnull List<String> text, @Nullable EntityPlayer player)
    {
        // Expiration dates
        if (isRotten())
        {
            text.add(TextFormatting.RED + I18n.format("tfc.tooltip.food_rotten"));
        }
        else
        {
            long rottenDate = getRottenDate();
            if (rottenDate == Long.MAX_VALUE)
            {
                text.add(TextFormatting.GOLD + I18n.format("tfc.tooltip.food_infinite_expiry"));
            }
            else
            {
                // Date food rots on.
                long rottenCalendarTime = rottenDate - CalendarTFC.PLAYER_TIME.getTicks() + CalendarTFC.CALENDAR_TIME.getTicks();
                // Days till food rots.
                long daysToRotInTicks = rottenCalendarTime - CalendarTFC.CALENDAR_TIME.getTicks();
                switch (ConfigTFC.Client.TOOLTIP.decayTooltipMode)
                {
                    case HIDE:
                        break;
                    case EXPIRATION_ONLY:
                        text.add(TextFormatting.DARK_GREEN + I18n.format("tfc.tooltip.food_expiry_date", ICalendarFormatted.getTimeAndDate(rottenCalendarTime, CalendarTFC.CALENDAR_TIME.getDaysInMonth())));
                        break;
                    case TIME_REMAINING_ONLY:
                        text.add(TextFormatting.BLUE + I18n.format("tfc.tooltip.food_expiry_date.days", String.valueOf(ICalendar.getTotalDays(daysToRotInTicks))));
                        break;
                    case ALL_INFO:
                        text.add(TextFormatting.DARK_GREEN + I18n.format("tfc.tooltip.food_expiry_date", ICalendarFormatted.getTimeAndDate(rottenCalendarTime, CalendarTFC.CALENDAR_TIME.getDaysInMonth())));
                        text.add(TextFormatting.BLUE + I18n.format("tfc.tooltip.food_expiry_date.days", String.valueOf(ICalendar.getTotalDays(daysToRotInTicks))));
                        break;
                }
            }
        }
        if (ConfigTFC.General.DEBUG.enable)
        {
            text.add("Created at " + getCreationDate());
        }

        // Nutrition / Hunger / Saturation / Water Values
        // Hide this based on the shift key (because it's a lot of into)
        if (GuiScreen.isShiftKeyDown())
        {
            text.add(TextFormatting.DARK_GREEN + I18n.format("tfc.tooltip.nutrition"));

            float saturation = getData().getSaturation();
            if (saturation > 0)
            {
                // This display makes it so 100% saturation means a full hunger bar worth of saturation.
                text.add(TextFormatting.GRAY + I18n.format("tfc.tooltip.nutrition_saturation", String.format("%d", (int) (saturation * 5))));
            }
            float water = getData().getWater();
            if (water > 0)
            {
                text.add(TextFormatting.GRAY + I18n.format("tfc.tooltip.nutrition_water", String.format("%d", (int) water)));
            }
            for (Nutrient nutrient : Nutrient.values())
            {
                float value = getData().getNutrients()[nutrient.ordinal()];
                if (value > 0)
                {
                    text.add(nutrient.getColor() + I18n.format("tfc.tooltip.nutrition_nutrient", I18n.format(Helpers.getEnumName(nutrient)), String.format("%.1f", value)));
                }
            }
        }
        else
        {
            text.add(TextFormatting.ITALIC + I18n.format("tfc.tooltip.hold_shift_for_nutrition_info"));
        }

        // Add info for each trait
        for (FoodTrait trait : getTraits())
        {
            trait.addTraitInfo(stack, text);
        }
    }
}
