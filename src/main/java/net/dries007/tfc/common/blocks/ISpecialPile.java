/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Note that this interface doesn't make the block in question pileable, necessarily. The tag tfc:can_be_snow_piled is still required.
 * The tag is intentionally left off of some blocks that don't maake sense to be piled.
 */
public interface ISpecialPile
{
    /**
     *  Called by {@link net.dries007.tfc.common.blockentities.PileBlockEntity} to 'hide' a different state than the one it is supposed to, to be revealed in the spring.
     */
    BlockState getHiddenState(BlockState internalState, boolean byPlayer);

    /**
     * In the case where there is a two-tall block.
     */
    @Nullable
    default BlockState getHiddenStateAbove(@Nullable BlockState aboveState, boolean byPlayer)
    {
        return aboveState;
    }
}
