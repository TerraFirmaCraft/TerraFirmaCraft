/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.put("item", item.save(new CompoundTag()));
        tag.putInt("count", count);
        tag.putInt("maxCount", maxCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        item = ItemStack.of(tag.getCompound("item"));
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

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos dropPos = worldPosition;
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            mutable.setWithOffset(worldPosition, d);
            if (Helpers.isBlock(level.getBlockState(mutable), TFCBlocks.BLOOMERY.get()))
            {
                dropPos = mutable.immutable();
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
        return count <= 0 ? Blocks.AIR.defaultBlockState() : level.getBlockState(worldPosition).setValue(BloomBlock.LAYERS, (int) Mth.clamp((float) count / maxCount * TOTAL_LAYERS, 1, 8));
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
