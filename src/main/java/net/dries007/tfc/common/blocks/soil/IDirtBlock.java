package net.dries007.tfc.common.blocks.soil;

import net.minecraft.block.BlockState;

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
