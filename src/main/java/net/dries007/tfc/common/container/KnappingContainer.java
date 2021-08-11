/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.IInventoryNoop;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.KnappingPattern;

public class KnappingContainer extends ItemStackContainer implements IButtonHandler, IInventoryNoop, ISlotCallback
{
    private static ItemStack getItemForKnapping(Inventory inv)
    {
        final ItemStack main = inv.player.getMainHandItem();
        return TFCTags.Items.KNAPPING_ANY.contains(main.getItem()) ? main : inv.player.getOffhandItem();
    }

    private final KnappingPattern matrix;
    private final boolean usesDisabledTex;
    private final SoundEvent sound;
    private final ItemStack stackCopy;
    private final int amountToConsume;
    private final boolean consumeAfterComplete;
    private final RecipeType<? extends KnappingRecipe> recipeType;
    private boolean requiresReset;
    private boolean hasBeenModified;
    private boolean hasConsumedIngredient;

    public KnappingContainer(MenuType<?> containerType, RecipeType<? extends KnappingRecipe> recipeType, int windowId, Inventory playerInv, int amountToConsume, boolean consumeAfterComplete, boolean usesDisabledTex, SoundEvent sound)
    {
        super(containerType, windowId, playerInv, getItemForKnapping(playerInv), 20);
        this.itemIndex += 1;
        this.amountToConsume = amountToConsume;
        this.usesDisabledTex = usesDisabledTex;
        this.consumeAfterComplete = consumeAfterComplete;
        this.recipeType = recipeType;
        this.sound = sound;

        matrix = new KnappingPattern();
        hasBeenModified = false;
        setRequiresReset(false);
        hasConsumedIngredient = false;
        stackCopy = this.stack.copy();
    }


    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        getMatrix().set(buttonID, false);

        if (!hasBeenModified)
        {
            if (!player.isCreative() && !consumeAfterComplete)
            {
                stack.shrink(amountToConsume);
            }
            hasBeenModified = true;
        }

        // check the pattern
        Slot slot = slots.get(0);
        if (slot != null && player.level instanceof ServerLevel)
        {
            KnappingRecipe recipe = getMatchingRecipe((ServerLevel) player.level);
            if (recipe != null)
            {
                slot.set(recipe.assemble(this));
            }
            else
            {
                slot.set(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void removed(Player player)
    {
        Slot slot = slots.get(0);
        ItemStack stack = slot.getItem();
        if (!stack.isEmpty())
        {
            if (!player.level.isClientSide)
            {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                consumeIngredientStackAfterComplete();
            }
        }
        super.removed(player);
    }

    /**
     * Used in client to check a slot state in the matrix
     *
     * @param index the slot index
     * @return the boolean state for the checked slot
     */
    public boolean getSlotState(int index)
    {
        return getMatrix().get(index);
    }

    /**
     * Used in client to set a slot state in the matrix
     *
     * @param index the slot index
     * @param value the value you wish to set the state to
     */
    public void setSlotState(int index, boolean value)
    {
        getMatrix().set(index, value);
    }

    public KnappingPattern getMatrix()
    {
        return matrix;
    }

    public boolean usesDisabledTexture()
    {
        return usesDisabledTex;
    }

    public SoundEvent getSound()
    {
        return sound;
    }

    public ItemStack getStackCopy()
    {
        return stackCopy;
    }

    public boolean requiresReset()
    {
        return requiresReset;
    }

    public void setRequiresReset(boolean requiresReset)
    {
        this.requiresReset = requiresReset;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return false;
    }

    @Override
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        resetMatrix();
    }

    @Override
    protected void addContainerSlots()
    {
        addSlot(new CallbackSlot(this, new ItemStackHandler(1), 0, 128, 46));
    }

    private void resetMatrix()
    {
        getMatrix().setAll(false);
        setRequiresReset(true);
        consumeIngredientStackAfterComplete();
    }

    @Nullable
    private KnappingRecipe getMatchingRecipe(ServerLevel level)
    {
        return level.getRecipeManager().getRecipeFor(recipeType, this, level).orElse(null);
    }

    protected void consumeIngredientStackAfterComplete()
    {
        if (consumeAfterComplete && !hasConsumedIngredient)
        {
            stack.shrink(amountToConsume);
            hasConsumedIngredient = true;
        }
    }
}
