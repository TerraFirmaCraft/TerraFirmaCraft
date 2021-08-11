/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * A no-op implementation of {@link net.minecraftforge.common.capabilities.Capability.IStorage} for capabilities that require custom serialize / deserialization logic
 *
 * @param <T> The capability class
 */
public final class NoopStorage<T> implements Capability.IStorage<T>
{
    @Nullable
    @Override
    public Tag writeNBT(Capability<T> capability, T instance, Direction side)
    {
        throw new UnsupportedOperationException("This storage is non functional. Do not use it.");
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt)
    {
        throw new UnsupportedOperationException("This storage is non functional. Do not use it.");
    }
}