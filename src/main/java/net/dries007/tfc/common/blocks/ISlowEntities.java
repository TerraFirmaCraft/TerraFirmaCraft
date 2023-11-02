/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface used by {@link net.dries007.tfc.util.Helpers#slowEntityInsideBlocks(Entity)} to indicate this block has a movement-slowing effect.
 */
public interface ISlowEntities
{
    float NO_SLOW = 1f;

    /**
     * @return A factor used to compute slowing effects, between [0, 1]. Lower values are slower.
     */
    float slowEntityFactor(BlockState state);
}
