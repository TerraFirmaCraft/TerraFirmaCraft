/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.capabilities.power.IRotator;
import net.dries007.tfc.util.mechanical.NetworkTracker;

public abstract class RotatingInventoryBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends TickableInventoryBlockEntity<C> implements IRotator
{
    protected final LazyOptional<IRotator> handler;

    protected long id = -1;

    public RotatingInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventory, Component defaultName)
    {
        super(type, pos, state, inventory, defaultName);
        handler = LazyOptional.of(() -> this);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        id = nbt.contains("network", Tag.TAG_LONG) ? nbt.getLong("network") : -1;
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("network", id);
        super.saveAdditional(nbt);
    }

    @Override
    public long getId()
    {
        return id;
    }

    @Override
    public void setId(long id)
    {
        this.id = id;
    }

    public void onRemoved()
    {
        NetworkTracker.onNodeUpdated(this);
    }

    public void onAdded()
    {
        NetworkTracker.onNodeAdded(this);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        onAdded();
    }

}
