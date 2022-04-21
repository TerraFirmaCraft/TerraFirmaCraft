/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Contract for a Bloomery inventory, for performing recipe matching.
 */
public interface BloomeryInventory extends EmptyInventory
{
    FluidStack getFluid();

    ItemStack getCatalyst();
}
