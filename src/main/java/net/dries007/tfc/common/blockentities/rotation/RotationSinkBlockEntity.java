/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

/**
 * This marks a sink which only has one incoming connection. This device is thus not possible to be invalid.
 */
@Deprecated
public interface RotationSinkBlockEntity extends RotatingBlockEntity
{
    @Override
    default void markAsInvalidInNetwork() {}

    @Override
    default boolean isInvalidInNetwork()
    {
        return false;
    }
}
