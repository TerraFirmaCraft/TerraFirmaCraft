/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.BookshelfBlock;
import net.dries007.tfc.util.Helpers;

public class BookshelfBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = Helpers.translatable("tfc.block_entity.bookshelf");

    public BookshelfBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<ItemStackHandler> inventoryFactory, Component defaultName)
    {
        super(type, pos, state, inventoryFactory, defaultName);
    }

    public BookshelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BOOKSHELF.get(), pos, state, defaultInventory(6), NAME);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.USABLE_IN_BOOKSHELF);
    }

    public InteractionResult use(Player player, ItemStack stack)
    {
        assert level != null;
        if (stack.isEmpty())
        {
            for (int i = inventory.getSlots() - 1; i >= 0; i--)
            {
                if (!inventory.getStackInSlot(i).isEmpty())
                {
                    if (!level.isClientSide)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(i, 1, false));
                    }
                    updateBookCount(i);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        else
        {
            for (int i = 0; i < inventory.getSlots(); i++)
            {
                if (inventory.getStackInSlot(i).isEmpty() && isItemValid(i, stack))
                {
                    ItemStack leftover = inventory.insertItem(i, stack.split(1), false);
                    if (!leftover.isEmpty() && !level.isClientSide)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, leftover);
                    }
                    updateBookCount(i);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        updateBookCount(slot);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (level != null)
        {
            level.scheduleTick(getBlockPos(), level.getBlockState(getBlockPos()).getBlock(), 1);
        }
    }

    public int countBooks()
    {
        int found = 0;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                found++;
            }
        }
        return found;
    }

    private void updateBookCount(int interactedSlot)
    {
        assert level != null;
        final int found = countBooks();
        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BookshelfBlock.BOOKS_STORED) && state.hasProperty(BookshelfBlock.LAST_INTERACTION_BOOK_SLOT))
        {
            if (state.getValue(BookshelfBlock.BOOKS_STORED) != found || state.getValue(BookshelfBlock.LAST_INTERACTION_BOOK_SLOT) != interactedSlot)
            {
                level.setBlockAndUpdate(worldPosition, state.setValue(BookshelfBlock.BOOKS_STORED, found).setValue(BookshelfBlock.LAST_INTERACTION_BOOK_SLOT, interactedSlot));
            }
        }
    }
}
