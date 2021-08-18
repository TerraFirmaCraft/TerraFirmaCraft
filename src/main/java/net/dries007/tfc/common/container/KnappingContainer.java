/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.KnappingPattern;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.inventory.IInventoryNoop;

public class KnappingContainer extends ItemStackContainer implements IButtonHandler, IInventoryNoop, ISlotCallback
{
    private static ItemStack getItemForKnapping(PlayerInventory inv)
    {
        final ItemStack main = inv.player.getMainHandItem();
        return main.getItem().is(TFCTags.Items.KNAPPING_ANY) ? main : inv.player.getOffhandItem();
    }

    private final KnappingPattern matrix;
    private final boolean usesDisabledTex;
    private final SoundEvent sound;
    private final ItemStack stackCopy;
    private final int amountToConsume;
    private final boolean consumeAfterComplete;
    private final IRecipeType<? extends KnappingRecipe> recipeType;
    private boolean requiresReset;
    private boolean hasBeenModified;
    private boolean hasConsumedIngredient;

    public KnappingContainer(ContainerType<?> containerType, IRecipeType<? extends KnappingRecipe> recipeType, int windowId, PlayerInventory playerInv, int amountToConsume, boolean consumeAfterComplete, boolean usesDisabledTex, SoundEvent sound)
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
    public void onButtonPress(int buttonID, @Nullable CompoundNBT extraNBT)
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
        if (slot != null && player.level instanceof ServerWorld)
        {
            KnappingRecipe recipe = getMatchingRecipe((ServerWorld) player.level);
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
    public void removed(PlayerEntity player)
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
    public void onSlotTake(PlayerEntity player, int slot, ItemStack stack)
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
    private KnappingRecipe getMatchingRecipe(ServerWorld level)
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
