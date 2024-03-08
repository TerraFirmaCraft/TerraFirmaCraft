/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Blocks that can be turned into their mud forms, including dirt and rooted dirt.
 */
public interface IMudBlock
{
    /**
     * Gets the mud block this block will transform into when interacted with
     * a {@link net.dries007.tfc.common.items.FluidContainerItem} that contains water.
     */
    BlockState getMud();
}
