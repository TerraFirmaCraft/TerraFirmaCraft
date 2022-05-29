/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.egg;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EggHandler implements IEgg, ICapabilitySerializable<CompoundTag>
{
    private final LazyOptional<IEgg> capability;
    private final ItemStack stack;

    private boolean fertilized;
    private long hatchDay;
    @Nullable
    private CompoundTag entityTag;

    private boolean initialized; // If the internal capability objects have loaded their data.

    public EggHandler(ItemStack itemStack)
    {
        stack = itemStack;
        fertilized = false;
        hatchDay = 0;
        entityTag = null;
        capability = LazyOptional.of(() -> this);
    }


    @Override
    public long getHatchDay()
    {
        return hatchDay;
    }

    @Override
    public Optional<Entity> getEntity(Level level)
    {
        return entityTag != null ? EntityType.create(entityTag, level) : Optional.empty();
    }

    @Override
    public boolean isFertilized()
    {
        return fertilized;
    }

    @Override
    public void setFertilized(@NotNull Entity entity, long hatchDay)
    {
        fertilized = true;
        entityTag = entity.serializeNBT();
        this.hatchDay = hatchDay;
        save();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == EggCapability.CAPABILITY)
        {
            load();
            return capability.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) { }

    private void load()
    {
        if (!initialized)
        {
            initialized = true;

            final CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("entity", Tag.TAG_COMPOUND))
            {
                entityTag = tag.getCompound("entity");
                fertilized = tag.getBoolean("fertilized");
                hatchDay = tag.getLong("hatch");
            }
            else
            {
                fertilized = false;
                entityTag = null;
                hatchDay = 0;
            }
        }
    }

    private void save()
    {
        final CompoundTag tag = stack.getOrCreateTag();
        if (entityTag != null)
        {
            tag.put("entity", entityTag);
            tag.putBoolean("fertilized", fertilized);
            tag.putLong("hatch", hatchDay);
        }
    }
}
