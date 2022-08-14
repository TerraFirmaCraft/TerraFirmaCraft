/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mock;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;

import org.jetbrains.annotations.NotNull;

public class MockCraftingContainer extends CraftingContainer
{
    public MockCraftingContainer(int width, int height)
    {
        super(new AbstractContainerMenu(null, 0)
        {
            @Override
            public boolean stillValid(@NotNull Player player)
            {
                return true;
            }

            @Override
            public void slotsChanged(@NotNull Container inventory) {}
        }, width, height);
    }
}
