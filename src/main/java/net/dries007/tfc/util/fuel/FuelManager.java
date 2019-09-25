/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.fuel;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.types.DefaultMetals;

public final class FuelManager
{
    private static final List<Fuel> FUELS = new ArrayList<>();
    private static final Fuel EMPTY = new Fuel(ItemStack.EMPTY, 0, 0);

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

    public static void postInit()
    {
        for (Tree wood : TFCRegistries.TREES.getValuesCollection())
        {
            BlockLogTFC log = BlockLogTFC.get(wood);
            FUELS.add(new Fuel(new ItemStack(log), wood.getBurnTicks(), wood.getBurnTemp()));
        }

        // Coal (Vanilla)
        FUELS.add(new Fuel(new ItemStack(Items.COAL, 1, 0), 2200, 1350f, true));
        // Coal (TFC Variants)
        FUELS.add(new Fuel(ItemOreTFC.get(TFCRegistries.ORES.getValue(DefaultMetals.BITUMINOUS_COAL), 1), 2200, 1415f, true));
        FUELS.add(new Fuel(ItemOreTFC.get(TFCRegistries.ORES.getValue(DefaultMetals.LIGNITE), 1), 2200, 1415f, true));

        // Charcoal
        FUELS.add(new Fuel(new ItemStack(Items.COAL, 1, 1), 1800, 1350f, true));

        //Peat
        FUELS.add(new Fuel(new ItemStack(BlocksTFC.PEAT), 2500, 680));
    }

    public static boolean addFuel(Fuel fuel)
    {
        if (FUELS.stream().anyMatch(x -> x.matchesInput(fuel)))
        {
            return false;
        }
        FUELS.add(fuel);
        return true;
    }
}
