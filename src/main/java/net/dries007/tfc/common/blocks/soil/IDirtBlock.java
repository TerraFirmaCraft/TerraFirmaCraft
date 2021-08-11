/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Blocks that grass can spread to.
 */
public interface IDirtBlock
{
    /**
     * Gets the grass state this dirt block may transform into, at the current location.
     * The returned block MUST be a {@link IGrassBlock}
     */
    BlockState getGrass();
}
