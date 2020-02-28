/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.properties;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface DrinkableProperty
{
    FluidProperty<DrinkableProperty> DRINKABLE = new FluidProperty<>("drinkable");

    void onDrink(@Nonnull EntityPlayer player);
}
