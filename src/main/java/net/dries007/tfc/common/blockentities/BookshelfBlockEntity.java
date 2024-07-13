/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class BookshelfBlockEntity extends ChiseledBookShelfBlockEntity
{
    public BookshelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }
}
