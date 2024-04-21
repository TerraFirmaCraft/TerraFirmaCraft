/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * This is primarily a marker interface to allow the barrel rack to detect that it can be replaced by a given item.
 */
public interface Rackable
{
    /**
     * Priority interaction with the rack block to allow replacing it via right click. Not required.
     * @return {@code true} the action succeeded.
     */
    default boolean useOnRack(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return false;
    }
}
