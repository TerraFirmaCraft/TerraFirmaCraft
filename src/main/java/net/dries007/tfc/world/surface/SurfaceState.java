/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Represents a wrapper around a context-sensitive block
 * For example, 'grass', which may be at time of placement, any variant of grass or sand depending on climate
 */
@FunctionalInterface
public interface SurfaceState
{
    BlockState getState(SurfaceBuilderContext context);

    default void setState(SurfaceBuilderContext context)
    {
        context.chunk().setBlockState(context.pos(), getState(context), false);
    }
}
