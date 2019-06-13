/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.functionalinterfaces;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface FacingChecker
{
    /**
     * implementation for hanging blocks to check if they can hang. 11/10 description.
     * facing is the direction the block should be pointing and the side
     * of the block it is supposed to hang ON, NOT the side it sticks WITH.
     * e.g: a sign facing north also hangs on the north side of the support block
     */
    FacingChecker canHangAt = (World world, BlockPos pos, EnumFacing facing) -> world.isSideSolid(pos.offset(facing.getOpposite()), facing);

    /**
     * Lambda for rotatable blocks to check if they can face a certain direction.
     * NOTE: where applicable, remember to still check if the blockstate allows for the specified direction!
     *
     * @param pos    position of the block that makes the check
     * @param facing the direction the block is supposed to face.
     * @return true if the side is valid, false otherwise.
     */
    boolean canFace(World world, BlockPos pos, EnumFacing facing);
}
