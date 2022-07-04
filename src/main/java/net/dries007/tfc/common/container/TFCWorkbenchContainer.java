/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class TFCWorkbenchContainer extends CraftingMenu
{
    private final ContainerLevelAccess access;

    public TFCWorkbenchContainer(int id, Inventory playerInventory)
    {
        super(id, playerInventory);
        this.access = ContainerLevelAccess.NULL;
    }

    public TFCWorkbenchContainer(int id, Inventory playerInventory, ContainerLevelAccess access)
    {
        super(id, playerInventory, access);
        this.access = access;
    }

    /**
     * Determines whether supplied player can use this container
     * TFC: use a tag instead of hard coding
     */
    @Override
    public boolean stillValid(Player playerIn)
    {
        return access.evaluate((world, pos) -> Helpers.isBlock(world.getBlockState(pos), TFCTags.Blocks.WORKBENCHES) && playerIn.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
