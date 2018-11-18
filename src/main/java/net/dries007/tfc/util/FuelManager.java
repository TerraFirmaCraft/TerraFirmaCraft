/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;

public class FuelManager
{
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

    public static void postInit()
    {
        for (Tree wood : TFCRegistries.TREES.getValuesCollection())
        {
            BlockLogTFC log = BlockLogTFC.get(wood);
            fuels.add(new Fuel(new ItemStack(log), wood.getBurnTicks(), wood.getBurnTemp()));
        }

        // todo: coal / charcoal
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
