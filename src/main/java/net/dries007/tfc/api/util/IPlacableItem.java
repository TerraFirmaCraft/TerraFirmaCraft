/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IPlacableItem
{
    /**
     * When an item that implements IPlacableItem is right clicked, this will be called to invoke special placement logic
     * Implement this if you want a custom item block that might need to subclass another item class
     *
     * @param world  The world
     * @param pos    The position that was clicked
     * @param stack  The player's held item stack
     * @param player The current player
     * @param facing The face of the block that was clicked
     * @param hitVec The hit vector
     * @return if the block was placed (will consume one item from the player's item)
     */
    boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing facing, Vec3d hitVec);
}
