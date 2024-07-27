/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;


import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.IngredientsComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.container.slot.CallbackSlot;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class SaladContainer extends Container implements ISlotCallback
{
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 4;
    public static final int SLOT_BOWLS = 5;
    public static final int SLOT_OUTPUT = 6;

    public static SaladContainer create(int windowId, Inventory inventory)
    {
        return new SaladContainer(windowId).init(inventory);
    }

    private final ItemStackHandler inventory;
    private boolean skipOutputUpdates = false;

    protected SaladContainer(int windowId)
    {
        super(TFCContainerTypes.SALAD.get(), windowId);

        this.inventory = new InventoryItemHandler(this, 7)
        {
            @Override
            protected void onContentsChanged(int slot) {}
        };
    }

    @Override
    public <C extends Container> C init(Inventory playerInventory, int yOffset)
    {
        final C self = super.init(playerInventory, yOffset);

        assert player != null;

        // move bowls from inventory to bowl slot
        ItemStack bowl = player.getMainHandItem();
        if (Helpers.isItem(bowl, TFCTags.Items.SALAD_BOWLS))
        {
            inventory.setStackInSlot(SLOT_BOWLS, bowl);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        else
        {
            bowl = player.getOffhandItem();
            if (Helpers.isItem(bowl, TFCTags.Items.SALAD_BOWLS))
            {
                inventory.setStackInSlot(SLOT_BOWLS, bowl);
                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }

        return self;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        if (index == SLOT_OUTPUT)
        {
            skipOutputUpdates = true;
        }
        final ItemStack result = super.quickMoveStack(player, index);
        skipOutputUpdates = false;
        return result;
    }

    @Override
    protected void addContainerSlots()
    {
        for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
        {
            addSlot(new CallbackSlot(this, inventory, i, 44 + 18 * i, 24));
        }

        addSlot(new CallbackSlot(this, inventory, SLOT_BOWLS, 44, 56));
        addSlot(new CallbackSlot(this, inventory, SLOT_OUTPUT, 116, 56));
    }

    @Override
    public void removed(Player player)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            final ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && i != SLOT_OUTPUT) // Skip the salad slot
            {
                giveItemStackToPlayerOrDrop(player, stack);
            }
        }

        super.removed(player);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return switch (slot)
            {
                case SLOT_BOWLS -> Helpers.isItem(stack, TFCTags.Items.SALAD_BOWLS);
                case SLOT_OUTPUT -> false;
                default -> Helpers.isItem(stack, TFCTags.Items.USABLE_IN_SALAD);
            };
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        // This batches output updates, as we don't want to get stuck in an update loop.
        if (!skipOutputUpdates)
        {
            final ItemStack bowlStack = inventory.getStackInSlot(SLOT_BOWLS);
            if (!bowlStack.isEmpty())
            {
                // At least one bowl exists, so create a salad
                float water = 0, saturation = 0;
                float[] nutrition = new float[Nutrient.TOTAL];
                int ingredientCount = 0; // The number of unique ingredients
                int minIngredientCount = 64; // The minimum stack size of the ingredients
                final List<ItemStack> ingredients = new ArrayList<>();
                for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
                {
                    final ItemStack ingredient = inventory.getStackInSlot(i);
                    final @Nullable IFood food = FoodCapability.get(ingredient);
                    if (food != null)
                    {
                        ingredients.add(ingredient.copyWithCount(1));
                        if (food.isRotten())
                        {
                            // Rotten food is not allowed
                            ingredientCount = 0;
                            break;
                        }

                        water += food.getData().water();
                        saturation += food.getData().saturation();

                        for (Nutrient nutrient : Nutrient.VALUES)
                        {
                            nutrition[nutrient.ordinal()] += food.getData().nutrient(nutrient);
                        }

                        ingredientCount++;
                        if (ingredient.getCount() < minIngredientCount)
                        {
                            minIngredientCount = ingredient.getCount();
                        }
                    }
                }

                if (bowlStack.getCount() < minIngredientCount)
                {
                    minIngredientCount = bowlStack.getCount();
                }

                if (ingredientCount > 0)
                {
                    final float multiplier = 0.75f; // salad multiplier
                    water *= multiplier;
                    saturation *= multiplier;

                    Nutrient maxNutrient = null;
                    float maxNutrientValue = 0;
                    for (Nutrient nutrient : Nutrient.values())
                    {
                        nutrition[nutrient.ordinal()] *= multiplier;
                        if (nutrition[nutrient.ordinal()] > maxNutrientValue)
                        {
                            maxNutrientValue = nutrition[nutrient.ordinal()];
                            maxNutrient = nutrient;
                        }
                    }

                    if (maxNutrient != null)
                    {
                        final ItemStack salad = new ItemStack(TFCItems.SALADS.get(maxNutrient).get(), minIngredientCount);
                        FoodCapability.setFoodForDynamicItemOnCreate(salad, FoodData.of(4, water, saturation, nutrition, 4.0f));
                        salad.set(TFCComponents.INGREDIENTS, IngredientsComponent.of(ingredients));
                        salad.set(TFCComponents.BOWL, Bowl.of(bowlStack));
                        inventory.setStackInSlot(SLOT_OUTPUT, salad);
                        return;
                    }
                }
            }

            // Failed to make a salad
            if (!inventory.getStackInSlot(SLOT_OUTPUT).isEmpty())
            {
                inventory.setStackInSlot(SLOT_OUTPUT, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case CONTAINER -> {
                    if (slotIndex != SLOT_OUTPUT) // todo: this needs to handle updating the salad but it's complicated I'll do it later to hell with it.
                    {
                        yield !moveItemStackTo(stack, containerSlots, containerSlots + 36, false);
                    }
                    yield true;
                }
                case HOTBAR, MAIN_INVENTORY -> !moveItemStackTo(stack, SLOT_INPUT_START, 1 + SLOT_BOWLS, false);
            };
    }

    @Override
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        if (slot == SLOT_OUTPUT)
        {
            // Decrement ingredients and bowls by the amount that was taken
            final int amountCreated = stack.getCount();
            for (int i = SLOT_INPUT_START; i <= SLOT_BOWLS; i++)
            {
                final ItemStack inputStack = inventory.getStackInSlot(i);
                inputStack.shrink(amountCreated);
            }
        }
    }
}
