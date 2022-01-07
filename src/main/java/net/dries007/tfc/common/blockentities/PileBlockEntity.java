package net.dries007.tfc.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class PileBlockEntity extends TFCBlockEntity
{
    private BlockState internalState;
    @Nullable private BlockState aboveState;

    public PileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.PILE.get(), pos, state);

        internalState = Blocks.AIR.defaultBlockState();
        aboveState = null;
    }

    public void setHiddenStates(BlockState internalState, @Nullable BlockState aboveState)
    {
        this.internalState = internalState;
        this.aboveState = aboveState;
    }

    public BlockState getInternalState()
    {
        return internalState;
    }

    @Nullable
    public BlockState getAboveState()
    {
        return aboveState;
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        internalState = NbtUtils.readBlockState(tag.getCompound("internalState"));
        aboveState = tag.contains("aboveState", Tag.TAG_COMPOUND) ? NbtUtils.readBlockState(tag.getCompound("aboveState")) : null;
        super.loadAdditional(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        tag.put("internalState", NbtUtils.writeBlockState(internalState));
        if (aboveState != null)
        {
            tag.put("aboveState", NbtUtils.writeBlockState(aboveState));
        }
        super.saveAdditional(tag);
    }
}
