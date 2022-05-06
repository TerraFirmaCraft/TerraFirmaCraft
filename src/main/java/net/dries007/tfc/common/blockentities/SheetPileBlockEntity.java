/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class SheetPileBlockEntity extends TFCBlockEntity
{
    private final List<ItemStack> stacks;
    private final List<Metal> cachedMetals;

    public SheetPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.SHEET_PILE.get(), pos, state);

        this.stacks = Helpers.listOf(ItemStack.EMPTY, 6);
        this.cachedMetals = Helpers.listOf(null, 6);
    }

    public void addSheet(Direction direction, ItemStack stack)
    {
        stacks.set(direction.ordinal(), stack);
        cachedMetals.set(direction.ordinal(), null);
        markForSync();
    }

    public ItemStack removeSheet(Direction direction)
    {
        final ItemStack stack = stacks.get(direction.ordinal());
        stacks.set(direction.ordinal(), ItemStack.EMPTY);
        cachedMetals.set(direction.ordinal(), null);
        markForSync();
        return stack;
    }

    /**
     * Returns a cached metal for the given side, if present, otherwise grabs from the cache.
     * The metal is defined by checking what metal the stack would melt into if heated.
     * Any other items turn into {@link Metal#unknown()}.
     */
    public Metal getOrCacheMetal(Direction direction)
    {
        @Nullable Metal metal = cachedMetals.get(direction.ordinal());
        if (metal == null)
        {
            final ItemStack stack = stacks.get(direction.ordinal());
            final ItemStackInventory inventory = new ItemStackInventory(stack);
            final @Nullable HeatingRecipe recipe = HeatingRecipe.getRecipe(inventory);
            if (recipe != null)
            {
                final FluidStack outputFluid = recipe.getOutputFluid(inventory);
                if (!outputFluid.isEmpty())
                {
                    metal = Metal.get(outputFluid.getFluid());
                }
            }

            if (metal == null)
            {
                metal = Metal.unknown();
            }
            cachedMetals.set(direction.ordinal(), metal);
        }
        return metal;
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        tag.put("stacks", Helpers.writeItemStacksToNbt(stacks));
        super.saveAdditional(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        Helpers.readItemStacksFromNbt(stacks, tag.getList("stacks", Tag.TAG_COMPOUND));
        super.loadAdditional(tag);
    }
}
