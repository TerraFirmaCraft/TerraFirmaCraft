/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.util.KnappingPattern;
import net.dries007.tfc.util.KnappingType;

public class KnappingContainer extends ItemStackContainer implements ButtonHandlerContainer, EmptyInventory, ISlotCallback
{
    public static final int SLOT_OUTPUT = 0;

    public static KnappingContainer create(ItemStack stack, KnappingType type, InteractionHand hand, Inventory playerInventory, int windowId)
    {
        return new KnappingContainer(TFCContainerTypes.KNAPPING.get(), windowId, playerInventory, stack, hand, type).init(playerInventory, 20);
    }

    private final KnappingType knappingType;

    private final KnappingPattern pattern;
    private final ItemStack originalStack;

    private boolean requiresReset;
    private boolean hasBeenModified;
    private boolean hasConsumedIngredient;

    public KnappingContainer(MenuType<?> containerType, int windowId, Inventory playerInv, ItemStack stack, InteractionHand hand, KnappingType knappingType)
    {
        super(containerType, windowId, playerInv, stack, hand);

        this.knappingType = knappingType;

        pattern = new KnappingPattern();
        hasBeenModified = false;
        hasConsumedIngredient = false;
        originalStack = stack.copy();

        setRequiresReset(false);
    }

    public KnappingType getKnappingType()
    {
        return knappingType;
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        // Set the matching patterns slot to clicked
        pattern.set(buttonID, false);

        // Maybe consume one of the input items, if we should
        if (!hasBeenModified)
        {
            if (!player.isCreative() && !knappingType.consumeAfterComplete())
            {
                stack.shrink(knappingType.amountToConsume());
            }
            hasBeenModified = true;
        }

        // Update the output slot based on the recipe
        final Slot slot = slots.get(SLOT_OUTPUT);
        if (player.level instanceof ServerLevel level)
        {
            slot.set(level.getRecipeManager().getRecipeFor(TFCRecipeTypes.KNAPPING.get(), this, level)
                .map(recipe -> recipe.assemble(this))
                .orElse(ItemStack.EMPTY));
        }
    }

    @Override
    public void removed(Player player)
    {
        final Slot slot = slots.get(SLOT_OUTPUT);
        final ItemStack stack = slot.getItem();
        if (!stack.isEmpty())
        {
            if (!player.level.isClientSide())
            {
                player.getInventory().placeItemBackInInventory(stack);
                consumeIngredientStackAfterComplete();
            }
        }
        super.removed(player);
    }

    public KnappingPattern getPattern()
    {
        return pattern;
    }

    public ItemStack getOriginalStack()
    {
        return originalStack;
    }

    @Override
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        resetPattern();
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
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, containerSlots + 36, true); // Hotbar first
                case HOTBAR -> !moveItemStackTo(stack, containerSlots, containerSlots + 27, false);
                case MAIN_INVENTORY -> !moveItemStackTo(stack, containerSlots + 27, containerSlots + 36, false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        addSlot(new CallbackSlot(this, new ItemStackHandler(1), 0, 128, 46));
    }

    private void resetPattern()
    {
        pattern.setAll(false);
        setRequiresReset(true);
        consumeIngredientStackAfterComplete();
    }

    protected void consumeIngredientStackAfterComplete()
    {
        if (knappingType.consumeAfterComplete() && !hasConsumedIngredient)
        {
            stack.shrink(knappingType.amountToConsume());
            hasConsumedIngredient = true;
        }
    }
}
