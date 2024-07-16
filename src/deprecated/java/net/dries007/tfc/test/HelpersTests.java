/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.util.Helpers;

public class HelpersTests extends TestHelper
{
    @Test
    public void testIterate()
    {
        final IItemHandlerModifiable inventory = new ItemStackHandler(5);
        inventory.setStackInSlot(1, new ItemStack(Items.APPLE));
        inventory.setStackInSlot(2, new ItemStack(Items.GOLD_INGOT));
        inventory.setStackInSlot(4, new ItemStack(Items.ITEM_FRAME));

        final List<ItemStack> iterated = Lists.newArrayList(Helpers.iterate(inventory));

        for (int i = 0; i < 5; i++)
        {
            assertEquals(inventory.getStackInSlot(i), iterated.get(i));
        }
    }
}
