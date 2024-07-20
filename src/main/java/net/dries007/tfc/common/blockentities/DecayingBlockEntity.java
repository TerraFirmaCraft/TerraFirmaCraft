/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.crop.DecayingBlock;
import net.dries007.tfc.common.component.food.FoodCapability;

public class DecayingBlockEntity extends TFCBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, DecayingBlockEntity decaying)
    {
        if (level.getGameTime() % 20 == 0 && decaying.isRotten() && state.getBlock() instanceof DecayingBlock block)
        {
            decaying.setStack(ItemStack.EMPTY);
            level.setBlockAndUpdate(pos, block.getRottedBlock().defaultBlockState());
        }
    }

    private ItemStack stack = ItemStack.EMPTY;

    public DecayingBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.DECAYING.get(), pos, state);
    }

    protected DecayingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        this.stack = ItemStack.parseOptional(provider, nbt.getCompound("item"));
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.saveAdditional(nbt, provider);
        nbt.put("item", stack.save(provider));
    }

    public boolean isRotten()
    {
        return stack.isEmpty() || FoodCapability.isRotten(stack);
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack.copyWithCount(1);
    }
}
