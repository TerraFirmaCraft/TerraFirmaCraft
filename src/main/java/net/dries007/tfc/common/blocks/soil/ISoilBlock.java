/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Blocks that can be turned into their underlying soil, such as farmland, paths and grass.
 */
public interface ISoilBlock
{
    /**
     * Gets the dirt block this block will transform into given the current state
     */
    BlockState getDirt();
}
