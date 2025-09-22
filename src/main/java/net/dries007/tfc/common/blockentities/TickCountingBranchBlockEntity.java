/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TickCountingBranchBlockEntity extends TickCounterBlockEntity
{
    public static void reset(Level level, BlockPos pos)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTING_BRANCH.get()).ifPresent(TickCounterBlockEntity::resetCounter);
    }

    public static void addTicks(Level level, BlockPos pos, long ticks)
    {
        Optional<TickCountingBranchBlockEntity> entity = level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTING_BRANCH.get());
        entity.ifPresent(branchBlockEntity -> branchBlockEntity.increaseCounter(ticks));
    }

    public static void setStemPos(Level level, BlockPos pos, BlockPos stemPos)
    {
        Optional<TickCountingBranchBlockEntity> entity = level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTING_BRANCH.get());
        entity.ifPresent(branchBlockEntity -> branchBlockEntity.setStemPos(stemPos));
    }

    private BlockPos stemPos;

    public TickCountingBranchBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.TICK_COUNTING_BRANCH.get(), pos, state);
    }

    public TickCountingBranchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        stemPos = pos;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        int[] stemArray = nbt.getIntArray("stemPos");
        stemPos = new BlockPos(stemArray[0], stemArray[1], stemArray[2]);
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putIntArray("stemPos", new int[] {stemPos.getX(), stemPos.getY(), stemPos.getZ()});
        super.saveAdditional(nbt, provider);
    }

    public void setStemPos(BlockPos stemPos)
    {
        this.stemPos = stemPos;
        setChanged();
    }

    public BlockPos getStemPos()
    {
        return stemPos;
    }
}
