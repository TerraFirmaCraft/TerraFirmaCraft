/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.util.KnappingPattern;
import org.jetbrains.annotations.Nullable;

public class KnappingContainer extends ItemStackContainer implements ButtonHandlerContainer, EmptyInventory, ISlotCallback
{
    public static final int SLOT_OUTPUT = 0;

    public static KnappingContainer createClay(ItemStack stack, InteractionHand hand, Inventory playerInventory, int windowId)
    {
        return new KnappingContainer(TFCContainerTypes.CLAY_KNAPPING.get(), TFCRecipeTypes.CLAY_KNAPPING.get(), windowId, playerInventory, stack, hand, 5, true, true, TFCSounds.KNAP_CLAY.get()).init(playerInventory, 20);
    }

    public static KnappingContainer createFireClay(ItemStack stack, InteractionHand hand, Inventory playerInventory, int windowId)
    {
        return new KnappingContainer(TFCContainerTypes.FIRE_CLAY_KNAPPING.get(), TFCRecipeTypes.FIRE_CLAY_KNAPPING.get(), windowId, playerInventory, stack, hand, 5, true, true, TFCSounds.KNAP_CLAY.get()).init(playerInventory, 20);
    }

    public static KnappingContainer createRock(ItemStack stack, InteractionHand hand, Inventory playerInventory, int windowId)
    {
        return new KnappingContainer(TFCContainerTypes.ROCK_KNAPPING.get(), TFCRecipeTypes.ROCK_KNAPPING.get(), windowId, playerInventory, stack, hand, 1, false, false, TFCSounds.KNAP_STONE.get()).init(playerInventory, 20);
    }

    public static LeatherKnappingContainer createLeather(ItemStack stack, InteractionHand hand, Inventory playerInventory, int windowId)
    {
        return new LeatherKnappingContainer(TFCContainerTypes.LEATHER_KNAPPING.get(), TFCRecipeTypes.LEATHER_KNAPPING.get(), windowId, playerInventory, stack, hand, 1, false, false, TFCSounds.KNAP_LEATHER.get()).init(playerInventory, 20);
    }

    private final int amountToConsume;
    private final boolean usesDisabledTex;
    private final boolean consumeAfterComplete;
    private final RecipeType<? extends KnappingRecipe> recipeType;
    private final SoundEvent sound;

    private final KnappingPattern pattern;
    private final ItemStack originalStack;

    private boolean requiresReset;
    private boolean hasBeenModified;
    private boolean hasConsumedIngredient;

    public KnappingContainer(MenuType<?> containerType, RecipeType<? extends KnappingRecipe> recipeType, int windowId, Inventory playerInv, ItemStack stack, InteractionHand hand, int amountToConsume, boolean consumeAfterComplete, boolean usesDisabledTex, SoundEvent sound)
    {
        super(containerType, windowId, playerInv, stack, hand);

        this.amountToConsume = amountToConsume;
        this.usesDisabledTex = usesDisabledTex;
        this.consumeAfterComplete = consumeAfterComplete;
        this.recipeType = recipeType;
        this.sound = sound;

        pattern = new KnappingPattern();
        hasBeenModified = false;
        hasConsumedIngredient = false;
        originalStack = stack.copy();

        setRequiresReset(false);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        // Set the matching patterns slot to clicked
        pattern.set(buttonID, false);

        // Maybe consume one of the input items, if we should
        if (!hasBeenModified)
        {
            if (!player.isCreative() && !consumeAfterComplete)
            {
                stack.shrink(amountToConsume);
            }
            hasBeenModified = true;
        }

        // Update the output slot based on the recipe
        final Slot slot = slots.get(SLOT_OUTPUT);
        if (player.level instanceof ServerLevel level)
        {
            slot.set(level.getRecipeManager().getRecipeFor(recipeType, this, level)
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

    public boolean usesDisabledTexture()
    {
        return usesDisabledTex;
    }

    public SoundEvent getSound()
    {
        return sound;
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
        if (consumeAfterComplete && !hasConsumedIngredient)
        {
            stack.shrink(amountToConsume);
            hasConsumedIngredient = true;
        }
    }
}
