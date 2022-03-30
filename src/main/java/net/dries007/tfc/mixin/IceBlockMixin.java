/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends HalfTransparentBlock
{
    private IceBlockMixin(Properties properties)
    {
        super(properties);
    }

    /**
     * Makes ice blocks stop rendering the faces of connected ice piles.
     */
    @Override
    public boolean skipRendering(BlockState state, BlockState otherState, Direction facing)
    {
        return Helpers.isBlock(otherState, TFCBlocks.ICE_PILE.get()) || super.skipRendering(state, otherState, facing);
    }
}
