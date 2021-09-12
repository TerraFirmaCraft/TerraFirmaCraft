/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BerryBushBlockEntity extends TickCounterBlockEntity
{
    private boolean isGrowing;
    private boolean harvested;
    private int useTicks;
    private int deathTicks;

    public BerryBushBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BERRY_BUSH.get(), pos, state);
    }

    protected BerryBushBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        harvested = true;
        useTicks = 0;
        deathTicks = 0;
        isGrowing = true;
    }

    @Override
    public void load(CompoundTag nbt)
    {
        isGrowing = nbt.getBoolean("isGrowing");
        harvested = nbt.getBoolean("harvested");
        useTicks = nbt.getInt("useTicks");
        deathTicks = nbt.getInt("deathTicks");
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putBoolean("isGrowing", isGrowing);
        nbt.putBoolean("harvested", harvested);
        nbt.putInt("useTicks", useTicks);
        nbt.putInt("deathTicks", deathTicks);
        return super.save(nbt);
    }

    public boolean isGrowing()
    {
        return isGrowing;
    }

    public void setGrowing(boolean growing)
    {
        isGrowing = growing;
    }

    public boolean isHarvested()
    {
        return harvested;
    }

    public void setHarvested(boolean isHarvested)
    {
        harvested = isHarvested;
    }

    public void use()
    {
        useTicks++;
    }

    public void stopUsing()
    {
        useTicks = 0;
    }

    public boolean willStopUsing()
    {
        return useTicks > 20;
    }

    public void addDeath()
    {
        deathTicks++;
    }

    public int getDeath()
    {
        return deathTicks;
    }

    public boolean willDie()
    {
        return deathTicks > 15;
    }

    public void resetDeath()
    {
        deathTicks = 0;
    }
}
