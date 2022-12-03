/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface Lightable
{
    default boolean lightBlock(Level level, BlockState state, BlockPos pos, boolean isStrong)
    {
        return this.lightBlock(level, state, pos, isStrong, null);
    }

    boolean lightBlock(Level level, BlockState state, BlockPos pos, boolean isStrong, @Nullable Entity entity);

}
