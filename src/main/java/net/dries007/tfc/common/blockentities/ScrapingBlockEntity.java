/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.dries007.tfc.util.Helpers;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ScrapingBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.scraping");
    @Nullable private ResourceLocation inputTexture = null;
    @Nullable private ResourceLocation outputTexture = null;
    private short positions = 0; // essentially a boolean[16]

    public ScrapingBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.SCRAPING.get(), pos, state, defaultInventory(1), NAME);
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
                final ItemStack currentItem = inventory.getStackInSlot(0);
                final ScrapingRecipe recipe = getRecipe(currentItem);
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
        inputTexture = nbt.contains("inputTexture", Tag.TAG_STRING) ? new ResourceLocation(nbt.getString("inputTexture")) : null;
        outputTexture = nbt.contains("outputTexture", Tag.TAG_STRING) ? new ResourceLocation(nbt.getString("outputTexture")) : null;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putShort("positions", positions);
        if (inputTexture != null) nbt.putString("inputTexture", inputTexture.toString());
        if (outputTexture != null) nbt.putString("outputTexture", outputTexture.toString());
        super.saveAdditional(nbt);
    }

    /**
     * @deprecated specify the fields input_texture and output_texture in the recipe json
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
    public ItemStack getCachedItem()
    {
        return ItemStack.EMPTY;
    }

    /**
     * @deprecated specify the fields input_texture and output_texture in the recipe json
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
    public void setCachedItem(ItemStack cachedItem) {}

    @Nullable
    public ResourceLocation getInputTexture()
    {
        return inputTexture;
    }

    @Nullable
    public ResourceLocation getOutputTexture()
    {
        return outputTexture;
    }

    public void updateDisplayCache()
    {
        if (!isComplete())
        {
            final ItemStack stack = inventory.getStackInSlot(0);
            final ScrapingRecipe recipe = getRecipe(stack);
            inputTexture = recipe == null ? null : recipe.getInputTexture();
            outputTexture = recipe == null ? null : recipe.getOutputTexture();
        }
    }

    @Nullable
    private ScrapingRecipe getRecipe(ItemStack stack)
    {
        assert level != null;
        return ScrapingRecipe.getRecipe(level, new ItemStackInventory(stack));
    }
}
