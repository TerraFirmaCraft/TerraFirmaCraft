/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ScrapingBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.scraping");
    private ItemStack cachedItem; // for visual purposes only
    private short positions; // essentially a boolean[16]

    public ScrapingBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.SCRAPING.get(), pos, state, defaultInventory(1), NAME);
        cachedItem = ItemStack.EMPTY;
        positions = 0;
        setCachedItem(ItemStack.EMPTY);
    }

    public boolean isComplete()
    {
        return positions == -1;
    }

    public short getScrapedPositions()
    {
        return positions;
    }

    public void onClicked(float hitX, float hitZ)
    {
        int xPos = (int) (hitX * 4);
        int zPos = (int) (hitZ * 4);
        positions |= 1 << (xPos + zPos * 4);

        assert level != null;
        if (!level.isClientSide)
        {
            if (isComplete())
            {
                ItemStack currentItem = inventory.getStackInSlot(0);
                ScrapingRecipe recipe = getRecipe(currentItem);
                if (recipe != null)
                {
                    inventory.setStackInSlot(0, recipe.assemble(new ItemStackInventory(currentItem)));
                }
            }
            markForBlockUpdate();
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return getRecipe(stack) != null;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        positions = nbt.getShort("positions");
        updateDisplayCache();
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putShort("positions", positions);
        super.saveAdditional(nbt);
    }

    public ItemStack getCachedItem()
    {
        return cachedItem;
    }

    public void setCachedItem(ItemStack cachedItem)
    {
        this.cachedItem = cachedItem;
    }

    private void updateDisplayCache()
    {
        if (!isComplete())
        {
            final ItemStack stack = inventory.getStackInSlot(0);
            ScrapingRecipe recipe = getRecipe(stack);
            setCachedItem(recipe == null ? ItemStack.EMPTY : recipe.getResultItem().copy());
        }
        else
        {
            setCachedItem(inventory.getStackInSlot(0));
        }
    }

    @Nullable
    private ScrapingRecipe getRecipe(ItemStack stack)
    {
        assert level != null;
        return ScrapingRecipe.getRecipe(level, new ItemStackInventory(stack));
    }
}
