/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.common.capabilities.food.TFCFoodStats;

public class PlayerData implements ICapabilitySerializable<CompoundTag>
{
    private final Player player;
    private final LazyOptional<PlayerData> capability;
    @Nullable private CompoundTag delayedFoodNbt;

    public PlayerData(Player player)
    {
        this.player = player;
        this.capability = LazyOptional.of(() -> this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return cap == PlayerDataCapability.CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag nbt = new CompoundTag();
        if (player.getFoodData() instanceof TFCFoodStats)
        {
            nbt.put("food", ((TFCFoodStats) player.getFoodData()).serializeToPlayerData());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        delayedFoodNbt = nbt.contains("food", Constants.NBT.TAG_COMPOUND) ? nbt.getCompound("food") : null;
        if (player.getFoodData() instanceof TFCFoodStats)
        {
            writeTo((TFCFoodStats) player.getFoodData());
        }
    }

    public void writeTo(TFCFoodStats stats)
    {
        if (delayedFoodNbt != null)
        {
            stats.deserializeFromPlayerData(delayedFoodNbt);
            delayedFoodNbt = null;
        }
    }
}
