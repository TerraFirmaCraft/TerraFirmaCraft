/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

/**
 * Implement on {@link BlockEntityContainer} (this is a CONTAINER) that can be infested.
 * Implement on a {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity} (this is a BLOCK ENTITY) that can be infested.
 */
public interface PestContainer
{
    default boolean canBeInfested()
    {
        return true;
    }
}
