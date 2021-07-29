/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.player;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.common.capabilities.food.TFCFoodStats;

public class PlayerData implements ICapabilitySerializable<CompoundNBT>
{
    private final PlayerEntity player;
    private final LazyOptional<PlayerData> capability;

    public PlayerData(PlayerEntity player)
    {
        this.player = player;
        this.capability = LazyOptional.of(() -> this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return cap == PlayerDataCapability.CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        if (player.getFoodData() instanceof TFCFoodStats)
        {
            nbt.put("food", ((TFCFoodStats) player.getFoodData()).serializeToPlayerData());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        CompoundNBT foodNbt = nbt.getCompound("food");
        if (player.getFoodData() instanceof TFCFoodStats)
        {
            ((TFCFoodStats) player.getFoodData()).deserializeFromPlayerData(foodNbt);
        }
    }
}
