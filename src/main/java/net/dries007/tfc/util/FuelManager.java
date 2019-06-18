/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.types.DefaultMetals;

public final class FuelManager
{
    public static final float CHARCOAL_BURN_TEMPERATURE = 1350f;

    private static final List<Fuel> fuels = new ArrayList<>();
    private static final Fuel EMPTY = new Fuel(ItemStack.EMPTY, 0, 0);

    @Nonnull
    public static Fuel getFuel(ItemStack stack)
    {
        return fuels.stream().filter(x -> x.matchesInput(stack)).findFirst().orElse(EMPTY);
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

    public static void postInit()
    {
        for (Tree wood : TFCRegistries.TREES.getValuesCollection())
        {
            BlockLogTFC log = BlockLogTFC.get(wood);
            fuels.add(new Fuel(new ItemStack(log), wood.getBurnTicks(), wood.getBurnTemp()));
        }

        // Coal (Vanilla)
        fuels.add(new Fuel(new ItemStack(Items.COAL, 1, 0), 8000, 1350f, true));
        // Coal (TFC Variants)
        fuels.add(new Fuel(ItemOreTFC.get(TFCRegistries.ORES.getValue(DefaultMetals.BITUMINOUS_COAL), 1), 8000, 1415f, true));
        fuels.add(new Fuel(ItemOreTFC.get(TFCRegistries.ORES.getValue(DefaultMetals.LIGNITE), 1), 8000, 1415f, true));

        // Charcoal
        fuels.add(new Fuel(new ItemStack(Items.COAL, 1, 1), 8000, 1350f, true));

        // todo: peat
    }

    public static boolean addFuel(Fuel fuel)
    {
        if (fuels.stream().anyMatch(x -> x.matchesInput(fuel)))
        {
            return false;
        }
        fuels.add(fuel);
        return true;
    }
}
