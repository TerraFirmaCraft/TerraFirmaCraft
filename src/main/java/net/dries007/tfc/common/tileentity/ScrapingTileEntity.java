/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.recipes.ItemStackRecipeWrapper;
import net.dries007.tfc.common.recipes.ScrapingRecipe;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ScrapingTileEntity extends InventoryTileEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.scraping");
    private ItemStack cachedItem; // for visual purposes only
    private short positions; // essentially a boolean[16]

    public ScrapingTileEntity()
    {
        super(TFCTileEntities.SCRAPING.get(), defaultInventory(1), NAME);
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
                    inventory.setStackInSlot(0, recipe.assemble(new ItemStackRecipeWrapper(currentItem)));
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
    public void load(BlockState state, CompoundTag nbt)
    {
        super.load(state, nbt);
        positions = nbt.getShort("positions");
        updateDisplayCache();
    }

    @Override
    @Nonnull
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putShort("positions", positions);
        return super.save(nbt);
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
        return ScrapingRecipe.getRecipe(level, new ItemStackRecipeWrapper(stack));
    }
}
