package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.capabilities.power.IRotator;
import net.dries007.tfc.util.mechanical.NetworkTracker;

public abstract class RotatingBlockEntity extends TFCBlockEntity implements IRotator
{
    protected int signal = 0;
    protected long id = -1;

    public RotatingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        id = tag.getLong("network");
        signal = tag.getInt("signal");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putLong("network", id);
        tag.putInt("signal", signal);
    }

    @Override
    public int getSignal()
    {
        return signal;
    }

    @Override
    public void setSignal(int signal)
    {
        this.signal = signal;
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
}
