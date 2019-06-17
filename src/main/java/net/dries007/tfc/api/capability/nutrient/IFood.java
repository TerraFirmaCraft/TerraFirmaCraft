/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nutrient;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.util.agriculture.Nutrient;
import net.dries007.tfc.world.classic.CalendarTFC;

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
     * Sets the creation date
     * Use to apply preservation over time
     * DO NOT TRY AND PRESERVE ALREADY ROTTEN FOOD
     * Example:
     * - A ceramic large vessel will tick randomly. Between each tick, it tracks a tick counter.
     * - On a tick, for each item in inventory, it will try and "preserve" the item based on it being in the vessel for the time between the last random tick and the current tick
     * - It then would call setCreationDate(getCreationDate() + ticksSinceLastRandomTick * decayModifier)
     * - A decay modifier of 0 is no preservation, a decay modifier = 1 is 100% preservation (i.e. never decaying)
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
        return getRottenDate() < CalendarTFC.getCalendarTime();
    }

    /**
     * Called when the player consumes this food item
     * Called from {@link CapabilityFood.EventHandler}
     *
     * @param player the player doing the consuming
     * @param stack  the stack being consumed
     */
    default void onConsumedByPlayer(@Nonnull EntityPlayer player, @Nonnull ItemStack stack)
    {
        IPlayerNutrients playerCap = player.getCapability(CapabilityFood.CAPABILITY_PLAYER_NUTRIENTS, null);
        if (playerCap != null)
        {
            if (isRotten() && !player.world.isRemote)
            {
                for (Supplier<PotionEffect> effectSupplier : CapabilityFood.getRottenFoodEffects())
                {
                    if (Constants.RNG.nextFloat() < 0.8)
                    {
                        player.addPotionEffect(effectSupplier.get());
                    }
                }
            }
            else
            {
                for (Nutrient nutrient : Nutrient.values())
                {
                    playerCap.addNutrient(nutrient, getNutrient(stack, nutrient));
                }
            }
        }
    }

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
            text.add(I18n.format("tfc.tooltip.food_rotten"));
        }
        else
        {
            text.add(I18n.format("tfc.tooltip.food_expiry_date", CalendarTFC.getTimeAndDate(getRottenDate())));
            // Show nutrient values if not rotten
            for (Nutrient nutrient : Nutrient.values())
            {
                text.add(nutrient.name().toLowerCase() + ": " + getNutrient(stack, nutrient));
            }
        }
        // todo: make this respect skills tiers
        // i.e. lowest level shows category, i.e. "Category: Grain"
        // next level shows which nutrients it has, i.e. "Category: Grain, Nutrients: Carbohydrates, Fat"
        // final level shows exact values, i.e. "Category: Grain, Nutrients: Carbohydrates (1.5), Fat (3.0)"
        if (ConfigTFC.GENERAL.debug)
        {
            text.add("Created at " + getCreationDate());
        }
    }
}
