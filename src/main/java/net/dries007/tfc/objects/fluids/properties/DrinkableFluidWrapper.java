/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.properties;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;

public class DrinkableFluidWrapper extends FluidWrapper
{
    private final Consumer<EntityPlayer> onDrinkFunction;

    public DrinkableFluidWrapper(@Nonnull Fluid fluid, boolean isDefault, Consumer<EntityPlayer> onDrinkFunction)
    {
        super(fluid, isDefault);
        this.onDrinkFunction = onDrinkFunction;
    }

    public void onDrink(EntityPlayer entity)
    {
        onDrinkFunction.accept(entity);
    }
}
