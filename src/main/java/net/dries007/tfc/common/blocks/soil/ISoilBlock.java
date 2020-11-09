package net.dries007.tfc.common.blocks.soil;

import net.minecraft.block.BlockState;

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
