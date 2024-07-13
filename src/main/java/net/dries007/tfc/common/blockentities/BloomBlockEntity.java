/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

public class BloomBlockEntity extends TFCBlockEntity
{
    public static final int TOTAL_LAYERS = 8;

    private ItemStack item;
    private int count;
    private int maxCount;

    public BloomBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOM.get(), pos, state);
        item = ItemStack.EMPTY;
        count = 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.put("item", item.save(provider));
        tag.putInt("count", count);
        tag.putInt("maxCount", maxCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        item = ItemStack.parseOptional(provider, tag.getCompound("item"));
        count = tag.getInt("count");
        maxCount = tag.getInt("maxCount");
    }

    public void setBloom(ItemStack item, int count)
    {
        if (count > 0)
        {
            assert level != null;
            this.item = item;
            this.count = count;
            this.maxCount = count;
            level.setBlockAndUpdate(worldPosition, getState());
        }
    }

    public boolean dropBloom()
    {
        assert level != null;

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos dropPos = worldPosition;
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            cursor.setWithOffset(worldPosition, direction);
            if (Helpers.isBlock(level.getBlockState(cursor), TFCBlocks.BLOOMERY.get()))
            {
                dropPos = cursor.immutable();
                break;
            }
        }

        count -= 1;
        ItemStack item = this.item.copy();
        item.setCount(1);
        Helpers.spawnItem(level, dropPos, item);
        return level.setBlock(worldPosition, getState(), level.isClientSide ? 11 : 3);
    }

    public BlockState getState()
    {
        assert level != null;
        if (count <= 0)
        {
            return Blocks.AIR.defaultBlockState();
        }
        final int layers = maxCount <= TOTAL_LAYERS
            ? count // Must be in [1, TOTAL_LAYERS], so we use the count directly
            : Mth.clamp(TOTAL_LAYERS * count / maxCount, 1, TOTAL_LAYERS); // Otherwise, scale based on the max count to discrete layers

        return getBlockState().setValue(BloomBlock.LAYERS, layers);
    }

    public ItemStack getItem()
    {
        return item;
    }

    public int getCount()
    {
        return count;
    }
}
