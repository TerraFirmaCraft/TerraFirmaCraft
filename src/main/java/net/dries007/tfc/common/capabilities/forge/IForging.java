/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import org.jetbrains.annotations.Nullable;

public interface IForging extends ICapabilityProvider
{
    int getWork();

    void setWork(int work);

    ForgeSteps getSteps();

    @Nullable AnvilRecipe getRecipe(Level level);

    void setRecipe(@Nullable AnvilRecipe recipe);

    /**
     * @param rule The rule to match
     * @return {@code true} if the current instance matches the provided rule.
     */
    boolean matches(ForgeRule rule);

    default boolean matches(ForgeRule[] rules)
    {
        for (ForgeRule rule : rules)
        {
            if (!matches(rule))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds (works) a step as the new most recent, updating the other three most recent steps.
     */
    void addStep(@Nullable ForgeStep step);

    /**
     * Resets the object's {@link IForging} components. Used if an item falls out of an anvil without getting worked
     * Purpose is to preserve stackability on items that haven't been worked yet.
     */
    void reset();

    /**
     * @return true if the item is workable
     */
    default boolean canWork(ItemStack stack)
    {
        return stack.getCapability(HeatCapability.CAPABILITY).map(heat -> heat.getTemperature() > heat.getForgingTemperature()).orElse(true);
    }

    /**
     * @return true if the item is weldable
     */
    default boolean canWeld(ItemStack stack)
    {
        return stack.getCapability(HeatCapability.CAPABILITY).map(heat -> heat.getTemperature() > heat.getWeldingTemperature()).orElse(true);
    }
}