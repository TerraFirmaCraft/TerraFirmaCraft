/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.ISpecialPile;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

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

    public void setHiddenStates(BlockState internalState, @Nullable BlockState aboveState, boolean byPlayer)
    {
        if (Helpers.isBlock(internalState, TFCTags.Blocks.CONVERTS_TO_HUMUS) && !byPlayer)
        {
            this.internalState = TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.HUMUS).get().defaultBlockState();
        }
        else if (internalState.getBlock() instanceof ISpecialPile special)
        {
            this.internalState = special.getHiddenState(internalState, byPlayer);
        }
        else
        {
            this.internalState = internalState;
        }

        if (aboveState != null && aboveState.getBlock() instanceof ISpecialPile special)
        {
            this.aboveState = special.getHiddenStateAbove(aboveState, byPlayer);
        }
        else
        {
            this.aboveState = aboveState;
        }
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        internalState = NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound("internalState"));
        aboveState = tag.contains("aboveState", Tag.TAG_COMPOUND) ? NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound("aboveState")) : null;
        super.loadAdditional(tag, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        tag.put("internalState", NbtUtils.writeBlockState(internalState));
        if (aboveState != null)
        {
            tag.put("aboveState", NbtUtils.writeBlockState(aboveState));
        }
        super.saveAdditional(tag, provider);
    }
}
