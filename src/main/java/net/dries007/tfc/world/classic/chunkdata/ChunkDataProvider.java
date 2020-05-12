/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.chunkdata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.util.Helpers;

public final class ChunkDataProvider implements ICapabilitySerializable<NBTTagCompound>
{
    @CapabilityInject(ChunkDataTFC.class)
    public static final Capability<ChunkDataTFC> CHUNK_DATA_CAPABILITY = Helpers.getNull();

    private final ChunkDataTFC instance = CHUNK_DATA_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CHUNK_DATA_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CHUNK_DATA_CAPABILITY ? CHUNK_DATA_CAPABILITY.cast(instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return (NBTTagCompound) CHUNK_DATA_CAPABILITY.writeNBT(instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        CHUNK_DATA_CAPABILITY.readNBT(instance, null, nbt);
    }
}
