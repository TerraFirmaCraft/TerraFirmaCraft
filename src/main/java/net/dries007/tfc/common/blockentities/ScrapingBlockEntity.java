/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class ScrapingBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.scraping");
    private static final float[] NO_COLOR = {1f, 1f, 1f};
    @Nullable private ResourceLocation inputTexture = null;
    @Nullable private ResourceLocation outputTexture = null;
    private short positions = 0; // essentially a boolean[16]
    @Nullable private DyeColor color1 = null;
    @Nullable private DyeColor color2 = null;

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
                    final ItemStack extraDrop = recipe.getExtraDrop().getSingleStack(currentItem);
                    if (!extraDrop.isEmpty())
                    {
                        Helpers.spawnItem(level, worldPosition, extraDrop);
                    }
                    inventory.setStackInSlot(0, recipe.assemble(currentItem));
                }
            }
            markForBlockUpdate();
        }
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public boolean dye(DyeColor color)
    {
        if (color1 == null)
        {
            color1 = color;
            markForBlockUpdate();
            return true;
        }
        else if (color2 == null)
        {
            color2 = color;
            markForBlockUpdate();;
            return true;
        }
        return false;
    }

    public int getColor1()
    {
        return color1 != null ? color1.getTextureDiffuseColor() : -1;
    }

    public int getColor2()
    {
        return color2 != null ? color2.getTextureDiffuseColor() : -1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return getRecipe(stack) != null;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        positions = nbt.getShort("positions");
        inputTexture = nbt.contains("inputTexture", Tag.TAG_STRING) ? Helpers.resourceLocation(nbt.getString("inputTexture")) : null;
        outputTexture = nbt.contains("outputTexture", Tag.TAG_STRING) ? Helpers.resourceLocation(nbt.getString("outputTexture")) : null;
        color1 = nbt.contains("color1", Tag.TAG_INT) ? DyeColor.byId(nbt.getInt("color1")) : null;
        color2 = nbt.contains("color2", Tag.TAG_INT) ? DyeColor.byId(nbt.getInt("color2")) : null;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putShort("positions", positions);
        if (inputTexture != null) nbt.putString("inputTexture", inputTexture.toString());
        if (outputTexture != null) nbt.putString("outputTexture", outputTexture.toString());
        if (color1 != null) nbt.putInt("color1", color1.getId());
        if (color2 != null) nbt.putInt("color2", color2.getId());
        super.saveAdditional(nbt, provider);
    }

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
        return ScrapingRecipe.getRecipe(stack);
    }
}
