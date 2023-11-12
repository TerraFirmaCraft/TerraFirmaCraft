/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.util.Helpers;

public class EncasedAxleBlockEntity extends AxleBlockEntity
{
    private ItemStack internalItem = ItemStack.EMPTY;

    public EncasedAxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.ENCASED_AXLE.get(), pos, state);
    }

    public EncasedAxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        internalItem = ItemStack.of(tag.getCompound("internalItem"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.put("internalItem", internalItem.save(new CompoundTag()));
    }

    public void setInternalItem(ItemStack stack)
    {
        internalItem = stack.copy();
    }

    public void beforeRemove()
    {
        assert level != null;
        if (!internalItem.isEmpty())
        {
            Helpers.spawnItem(level, getBlockPos(), internalItem.copy());
        }
    }
}
