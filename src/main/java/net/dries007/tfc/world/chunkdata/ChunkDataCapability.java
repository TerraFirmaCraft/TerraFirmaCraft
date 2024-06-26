/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

/**
 * A wrapper to allow mutable access to the underlying capability, due to the semantics of how promotion of proto chunks
 * to level chunks are handled. Access through {@link ChunkData} instead of using this.
 * todo: 1.21, remove
 */
public class ChunkDataCapability implements ICapabilitySerializable<CompoundTag>
{
    @Deprecated public static final Capability<ChunkDataCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = Helpers.identifier("chunk_data");

    private final LazyOptional<ChunkDataCapability> capability;
    private ChunkData data;

    public ChunkDataCapability(ChunkData data)
    {
        this.capability = LazyOptional.of(() -> this);
        this.data = data;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return data.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        data.deserializeNBT(nbt);
    }

    void setData(ChunkData data)
    {
        this.data = data;
    }

    ChunkData getData()
    {
        return this.data;
    }
}
