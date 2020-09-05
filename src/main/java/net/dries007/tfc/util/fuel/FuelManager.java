/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.fuel;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public final class FuelManager
{
    private static final List<Fuel> FUELS = new ArrayList<>();
    private static final Fuel EMPTY = new Fuel(IIngredient.empty(), 0, 0);

    @Nonnull
    public static Fuel getFuel(ItemStack stack)
    {
        return FUELS.stream().filter(x -> x.matchesInput(stack)).findFirst().orElse(EMPTY);
    }

    public static boolean isItemFuel(ItemStack stack)
    {
        return getFuel(stack) != EMPTY;
    }

    public static boolean isItemForgeFuel(ItemStack stack)
    {
        Fuel fuel = getFuel(stack);
        return fuel != EMPTY && fuel.isForgeFuel();
    }

    public static boolean isItemBloomeryFuel(ItemStack stack)
    {
        Fuel fuel = getFuel(stack);
        return fuel != EMPTY && fuel.isBloomeryFuel();
    }

    public static void postInit()
    {
        for (Tree wood : TFCRegistries.TREES.getValuesCollection())
        {
            BlockLogTFC log = BlockLogTFC.get(wood);
            FUELS.add(new Fuel(IIngredient.of(new ItemStack(log)), wood.getBurnTicks(), wood.getBurnTemp()));
        }

        // Coals
        FUELS.add(new Fuel(IIngredient.of("gemCoal"), 2200, 1415f, true, false));
        FUELS.add(new Fuel(IIngredient.of("gemLignite"), 2000, 1350f, true, false));

        // Charcoal
        FUELS.add(new Fuel(IIngredient.of("charcoal"), 1800, 1350f, true, true));

        // Peat
        FUELS.add(new Fuel(IIngredient.of("peat"), 2500, 680));

        // Stick Bundle
        FUELS.add(new Fuel(IIngredient.of("stickBundle"), 600, 900));
    }

    /**
     * Register a new fuel only if the fuel is unique
     *
     * @param fuel the fuel obj to register
     */
    public static void addFuel(Fuel fuel)
    {
        if (canRegister(fuel))
        {
            FUELS.add(fuel);
        }
    }

    /**
     * Checks if this fuel can be registered
     *
     * @param fuel the fuel obj to register
     * @return true if the new fuel is unique (eg: don't have at least one itemstack that is equal to another already registered fuel)
     */
    public static boolean canRegister(Fuel fuel)
    {
        return FUELS.stream().noneMatch(x -> x.matchesInput(fuel));
    }
}
