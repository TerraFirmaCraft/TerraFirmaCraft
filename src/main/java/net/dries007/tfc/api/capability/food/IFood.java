/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendarFormatted;

/**
 * Capability for any food item
 * Allows foods to have nutrients, and also to decay / rot
 */
public interface IFood extends INBTSerializable<NBTTagCompound>
{
    /**
     * Gets the nutrient value (only a single item, not the sum of the stack)
     *
     * @param stack    the stack to get the nutrient of
     * @param nutrient the nutrient in question
     * @return a value, current range is around 0 - 3
     */
    float getNutrient(ItemStack stack, Nutrient nutrient);

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
     * How much thirst is restored when this item is eaten.
     * Drinking water from a water source gives 15, for reference
     *
     * @return a thirst value, roughly in the range 0 - 15
     */
    float getWater();

    /**
     * This is basically a saturation modifier
     *
     * @return a value roughly in the range 0.0 - 1.0
     */
    float getCalories();

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
    default void addNutrientInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
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
                // Calculate the date to display in calendar time
                long rottenCalendarTime = rottenDate - CalendarTFC.PLAYER_TIME.getTicks() + CalendarTFC.CALENDAR_TIME.getTicks();
                text.add(TextFormatting.DARK_GREEN + I18n.format("tfc.tooltip.food_expiry_date", ICalendarFormatted.getTimeAndDate(rottenCalendarTime, CalendarTFC.CALENDAR_TIME.getDaysInMonth())));

                if (ConfigTFC.GENERAL.debug)
                {
                    // todo: make this 1) use color, 2) only show if sneaking (to avoid tooltip clutter), 3) only show if skill is high enough
                    // i.e. lowest level shows category, i.e. "Category: Grain"
                    // next level shows which nutrients it has, i.e. "Category: Grain, Nutrients: Carbohydrates, Fat"
                    // final level shows exact values, i.e. "Category: Grain, Nutrients: Carbohydrates (1.5), Fat (3.0)"
                    for (Nutrient nutrient : Nutrient.values())
                    {
                        float amount = getNutrient(stack, nutrient);
                        if (amount > 0)
                        {
                            text.add(nutrient.name().toLowerCase() + ": " + amount);
                        }
                    }
                }
            }
        }
        if (ConfigTFC.GENERAL.debug)
        {
            text.add("Created at " + getCreationDate());
        }

        // Add info for each trait
        for (FoodTrait trait : getTraits())
        {
            trait.addTraitInfo(stack, text);
        }
    }
}
