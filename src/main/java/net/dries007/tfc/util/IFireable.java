/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.Metal;

/**
 * Must be on Item or Block
 * 
 * This allows the fireable item to do two things:
 * 1) be placed via shift + right click,
 * 2) use special handling for the pit kiln recipe (a recipe is still required)
 */
public interface IFireable
{
    /**
     * Get the minimum required tier of the device for firing
     * Pit Kiln placement requires this to be at most Tier I
     *
     * @return a tier
     */
    default Metal.Tier getTier()
    {
        return Metal.Tier.TIER_I;
    }

    /**
     * Gets the result of the item after being fired
     *
     * @param stack The item in question
     * @param tier  The tier of the firing device (Pit Kiln is {@link Metal.Tier#TIER_I})
     * @return a new item stack
     */
    ItemStack getFiringResult(ItemStack stack, Metal.Tier tier);
}
