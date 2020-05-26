/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.food.Nutrient;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.capability.ItemStackHandlerCallback;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.items.food.ItemDynamicBowlFood;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.agriculture.Food;

/**
 * We don't extend the item stack container because it's not linked to the item stack
 */
@ParametersAreNonnullByDefault
public class ContainerSalad extends ContainerSimple implements ISlotCallback
{
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 4;
    public static final int SLOT_BOWLS = 5;
    public static final int SLOT_OUTPUT = 6;

    private final ItemStackHandler inventory;
    private boolean skipOutputUpdates = false;

    public ContainerSalad(InventoryPlayer playerInv)
    {
        inventory = new ItemStackHandlerCallback(this, 7)
        {
            @Override
            protected void onContentsChanged(int slot) {}
        };

        // move bowls from inventory to bowl slot
        ItemStack bowl = playerInv.player.getHeldItemMainhand();
        if (OreDictionaryHelper.doesStackMatchOre(bowl, "bowl"))
        {
            inventory.setStackInSlot(SLOT_BOWLS, bowl);
            playerInv.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }
        else
        {
            bowl = playerInv.player.getHeldItemOffhand();
            if (OreDictionaryHelper.doesStackMatchOre(bowl, "bowl"))
            {
                inventory.setStackInSlot(SLOT_BOWLS, bowl);
                playerInv.player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
            }
        }

        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && i != SLOT_OUTPUT) // Skip the salad slot
            {
                ItemHandlerHelper.giveItemToPlayer(playerIn, stack);
            }
        }
        super.onContainerClosed(playerIn);
    }

    public ItemStack onTakeSalad(ItemStack saladStackTaken)
    {
        // Decrement ingredients and bowls by that amount
        int amountCreated = saladStackTaken.getCount();
        for (int i = SLOT_INPUT_START; i <= SLOT_BOWLS; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            stack.shrink(amountCreated);
        }
        return saladStackTaken;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_BOWLS:
                return OreDictionaryHelper.doesStackMatchOre(stack, "bowl");
            case SLOT_OUTPUT:
                return false;
            default:
                return stack.hasCapability(CapabilityFood.CAPABILITY, null) && Food.Category.doesStackMatchCategories(stack, Food.Category.VEGETABLE, Food.Category.FRUIT, Food.Category.COOKED_MEAT, Food.Category.DAIRY);
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        // This batches output updates, as we don't want to get stuck in an update loop.
        if (!skipOutputUpdates)
        {
            ItemStack bowlStack = inventory.getStackInSlot(SLOT_BOWLS);
            if (!bowlStack.isEmpty())
            {
                // At least one bowl exists, so create a salad
                float water = 0, saturation = 0;
                float[] nutrition = new float[Nutrient.TOTAL];
                int ingredientCount = 0; // The number of unique ingredients
                int minIngredientCount = 64; // The minimum stack size of the ingredients
                for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
                {
                    ItemStack ingredient = inventory.getStackInSlot(i);
                    IFood food = ingredient.getCapability(CapabilityFood.CAPABILITY, null);
                    if (food != null)
                    {
                        if (food.isRotten())
                        {
                            // Rotten food is not allowed
                            ingredientCount = 0;
                            break;
                        }
                        water += food.getData().getWater();
                        saturation += food.getData().getSaturation();
                        float[] ingredientNutrition = food.getData().getNutrients();
                        for (Nutrient nutrient : Nutrient.values())
                        {
                            nutrition[nutrient.ordinal()] += ingredientNutrition[nutrient.ordinal()];
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
                    float multiplier = 0.75f; // salad multiplier
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
                        ItemStack salad = new ItemStack(getSaladItem(maxNutrient), minIngredientCount);
                        IFood saladCap = salad.getCapability(CapabilityFood.CAPABILITY, null);
                        if (saladCap instanceof ItemDynamicBowlFood.DynamicFoodHandler)
                        {
                            saladCap.setCreationDate(CapabilityFood.getRoundedCreationDate());
                            ((ItemDynamicBowlFood.DynamicFoodHandler) saladCap).initCreationDataAndBowl(bowlStack.copy().splitStack(1), new FoodData(4, water, saturation, nutrition, Food.SALAD_VEGETABLE.getData().getDecayModifier()));
                        }
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

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index)
    {
        // Slot that was clicked
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ItemStack stackCopy = stack.copy();

            // Transfer out of the container
            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
            if (index < containerSlots)
            {
                if (index == SLOT_OUTPUT)
                {
                    skipOutputUpdates = true;
                }
                if (!mergeItemStack(stack, containerSlots, inventorySlots.size(), true))
                {
                    skipOutputUpdates = false;
                    return ItemStack.EMPTY;
                }
            }
            // Transfer into the container
            else if (!mergeItemStack(stack, SLOT_INPUT_START, SLOT_BOWLS + 1, false))
            {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (stack.getCount() == stackCopy.getCount())
            {
                skipOutputUpdates = false;
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stackCopy);
            skipOutputUpdates = false;
            return stackCopy;
        }
        return ItemStack.EMPTY;
    }

    private void addContainerSlots()
    {
        for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
        {
            addSlotToContainer(new SlotCallback(inventory, i, 44 + 18 * i, 24, this));
        }
        addSlotToContainer(new SlotCallback(inventory, SLOT_BOWLS, 44, 56, this));
        addSlotToContainer(new SlotCallback(inventory, SLOT_OUTPUT, 116, 56, this)
        {
            @Nonnull
            @Override
            public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack stack)
            {
                return ContainerSalad.this.onTakeSalad(stack);
            }
        });
    }

    @Nonnull
    private Item getSaladItem(Nutrient nutrient)
    {
        switch (nutrient)
        {
            case GRAIN:
                return ItemFoodTFC.get(Food.SALAD_GRAIN);
            case VEGETABLES:
                return ItemFoodTFC.get(Food.SALAD_VEGETABLE);
            case FRUIT:
                return ItemFoodTFC.get(Food.SALAD_FRUIT);
            case PROTEIN:
                return ItemFoodTFC.get(Food.SALAD_MEAT);
            default:
                return ItemFoodTFC.get(Food.SALAD_DAIRY);
        }
    }
}
