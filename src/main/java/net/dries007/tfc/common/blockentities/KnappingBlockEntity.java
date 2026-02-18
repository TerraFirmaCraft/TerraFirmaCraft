/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.KnappingPattern;
import net.dries007.tfc.util.data.KnappingType;

public class KnappingBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    // Rock stack represents the 2 items we're knapping together.
    ItemStack rockStack = getInventory().getStackInSlot(0);
    /**
     * 25-bit field representing the 5x5 knapping grid.
     * Bit index: x + z * 5
     * 1 = tile is present (has not been knocked off), 0 = tile is gone
     * Starts as (1 << 25) - 1 = all tiles present.
     */
    private int positions = (1 << 25) - 1;

    @Nullable private ResourceLocation texture = null;

    public KnappingBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.KNAPPING.get(), pos, state, defaultInventory(1));
    }

    /**
     * Called when the player right-clicks a tile on the block.
     * Maps the 3D hit position (0-1 on XZ plane) to a 5x5 grid cell and clears that bit.
     */
    public void onClicked(float hitX, float hitZ)
    {
        final ItemStack originalStack = inventory.getStackInSlot(0);
        if (!originalStack.isEmpty()) {
            rockStack = originalStack.copy();
            // We conceptually have 2 rocks: One for the item in this inventory, and 1 for the item in the players hand.
            rockStack.setCount(rockStack.getCount() + 1);
            getInventory().setStackInSlot(0, ItemStack.EMPTY);
        };

        // Destroy the stone in this inventory once we start knapping
        final int xPos = Math.min((int) (hitX * 5), 4);
        final int zPos = Math.min((int) (hitZ * 5), 4);

        final int index = xPos + zPos * 5;

        // Clear the bit for this tile (knock it off)
        positions &= ~(1 << index);

        if (level != null && !level.isClientSide)
        {
            checkRecipes();
            markForSync();
        }
        if (level != null)
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /**
     * 90° clockwise rotation of a 5×5 bitfield.
     * Mapping: (col, row) → (4 - row, col)
     * i.e. old index (col + row*5) → new index ((4-row) + col*5)
     */
    private static int rotateCW90(int pos)
    {
        int rotated = 0;
        for (int col = 0; col < 5; col++)
        {
            for (int row = 0; row < 5; row++)
            {
                if (((pos >> (col + row * 5)) & 1) == 1)
                {
                    rotated |= 1 << ((4 - row) + col * 5);
                }
            }
        }
        return rotated;
    }

    /**
     * After each click, check if the current pattern (or any of its 3 rotations) matches a knapping recipe.
     * Trying all 4 rotations means the player does not need to remember which direction they placed the block —
     * a pattern entered from any approach angle will still find its recipe.
     */
    private void checkRecipes()
    {
        assert level != null;

        // KnappingType.get() uses SizedIngredient.test() which requires stack.getCount() >= inputItem.count().
        // We store only 1 rock (the ground rock), but the tfc:rock type requires count=2 for the GUI.
        // Bypass the count check by testing the underlying Ingredient directly.
        @Nullable KnappingType type = null;
        for (KnappingType kt : KnappingType.MANAGER.getValues())
        {
            if (kt.inputItem().ingredient().test(rockStack))
            {
                type = kt;
                break;
            }
        }
        if (type == null) return;

        // Build all 4 rotations (0°, 90°, 180°, 270° CW) of the current positions bitfield
        final int rot0   = positions;
        final int rot90  = rotateCW90(rot0);
        final int rot180 = rotateCW90(rot90);
        final int rot270 = rotateCW90(rot180);

        final List<RecipeHolder<KnappingRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(TFCRecipeTypes.KNAPPING.get());

        for (int rotPositions : new int[] { rot0, rot90, rot180, rot270 })
        {
            final KnappingPattern currentPattern = new KnappingPattern();
            for (int i = 0; i < 25; i++)
            {
                currentPattern.set(i, (rotPositions >> i & 1) == 1);
            }

            for (RecipeHolder<KnappingRecipe> holder : recipes)
            {
                final KnappingRecipe recipe = holder.value();
                if (recipe.knappingType().get() == type
                    && currentPattern.matches(recipe.getPattern())
                    && recipe.matchesItem(rockStack))
                {
                    // Match found: clear inventory to prevent ejectInventory() from double-dropping the rock
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                    Helpers.spawnItem(level, worldPosition, recipe.assemble());
                    level.removeBlock(worldPosition, false);
                    return;
                }
            }
        }
    }

    public int getPositions()
    {
        return positions;
    }

    @Nullable
    public ResourceLocation getTexture()
    {
        return texture;
    }

    public void setTexture(@Nullable ResourceLocation texture)
    {
        this.texture = texture;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        // Items are inserted programmatically, not by player
        return false;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        positions = nbt.getInt("positions");
        texture = nbt.contains("texture", Tag.TAG_STRING) ? Helpers.resourceLocation(nbt.getString("texture")) : null;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putInt("positions", positions);
        if (texture != null) nbt.putString("texture", texture.toString());
        super.saveAdditional(nbt, provider);
    }
}
