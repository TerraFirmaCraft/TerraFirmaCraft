/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

import net.dries007.tfc.common.TFCTags;

public class TFCWorkbenchContainer extends CraftingMenu
{
    private final ContainerLevelAccess access;

    public TFCWorkbenchContainer(int id, Inventory playerInventory)
    {
        super(id, playerInventory);
        this.access = ContainerLevelAccess.NULL;
    }

    public TFCWorkbenchContainer(int id, Inventory playerInventory, ContainerLevelAccess worldPos)
    {
        super(id, playerInventory, worldPos);
        this.access = worldPos;
    }

    /**
     * Determines whether supplied player can use this container
     * TFC: use a tag instead of hardcoding
     */
    @Override
    public boolean stillValid(Player playerIn)
    {
        return access.evaluate((world, pos) -> world.getBlockState(pos).is(TFCTags.Blocks.WORKBENCH) && playerIn.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
