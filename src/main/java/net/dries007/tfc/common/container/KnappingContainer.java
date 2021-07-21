package net.dries007.tfc.common.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.IInventoryNoop;
import net.dries007.tfc.common.recipes.SimpleCraftMatrix;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.util.Helpers;

public class KnappingContainer extends ItemStackContainer implements IButtonHandler, IInventoryNoop
{
    public final SimpleCraftMatrix matrix;
    private final int amountToConsume;
    public final boolean usesDisabledTex;
    private final boolean needsKnife;
    private final boolean consumeAfterComplete;
    public final SoundEvent sound;
    public boolean requiresReset;
    private boolean hasBeenModified;
    private boolean hasConsumedIngredient;
    private final IRecipeType<? extends KnappingRecipe> recipeType;
    public final ItemStack stackCopy;

    public KnappingContainer(ContainerType<?> containerType, IRecipeType<? extends KnappingRecipe> recipeType, int windowId, PlayerInventory playerInv, int amountToConsume, boolean consumeAfterComplete, boolean usesDisabledTex, boolean needsKnife, SoundEvent sound)
    {
        super(containerType, windowId, playerInv, playerInv.player.getMainHandItem()); //todo make this work for offhand
        this.itemIndex += 1;
        this.amountToConsume = amountToConsume;
        this.usesDisabledTex = usesDisabledTex;
        this.consumeAfterComplete = consumeAfterComplete;
        this.needsKnife = needsKnife;
        this.recipeType = recipeType;
        this.sound = sound;

        matrix = new SimpleCraftMatrix();
        hasBeenModified = false;
        requiresReset = false;
        hasConsumedIngredient = false;
        stackCopy = this.stack.copy();
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundNBT extraNBT)
    {
        matrix.set(buttonID, false);

        if (!hasBeenModified)
        {
            if (!player.isCreative() && !consumeAfterComplete)
            {
                ItemStack consumedStack = Helpers.consumeItem(this.stack, amountToConsume);
                if (isOffhand)
                {
                    player.setItemInHand(Hand.OFF_HAND, consumedStack);
                }
                else
                {
                    player.setItemInHand(Hand.MAIN_HAND, consumedStack);
                }
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
    protected void addContainerSlots()
    {
        addSlot(new RunnableSlot(new ItemStackHandler(1), 0, 128, 44, this::resetMatrix));
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
     * JEI won't cause issues anymore see https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/718
     *
     * @param index the slot index
     * @return the boolean state for the checked slot
     */
    public boolean getSlotState(int index)
    {
        return matrix.get(index);
    }

    /**
     * Used in client to set a slot state in the matrix
     * JEI won't cause issues anymore see https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/718
     *
     * @param index the slot index
     * @param value the value you wish to set the state to
     */
    public void setSlotState(int index, boolean value)
    {
        matrix.set(index, value);
    }

    @Override
    protected void addPlayerInventorySlots(PlayerInventory playerInv)
    {
        // Add Player Inventory Slots (lower down)
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 20));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlot(new Slot(playerInv, k, 8 + k * 18, 142 + 20));
        }
    }

    private void resetMatrix()
    {
        matrix.setAll(false);
        requiresReset = true;
        consumeIngredientStackAfterComplete();
    }

    @Nullable
    private KnappingRecipe getMatchingRecipe(ServerWorld level)
    {
        return level.getRecipeManager().getRecipeFor(recipeType, this, level).orElse(null);
    }

    private void consumeIngredientStackAfterComplete()
    {
        if (consumeAfterComplete && !hasConsumedIngredient)
        {
            ItemStack stack = Helpers.consumeItem(this.stack, amountToConsume);
            if (isOffhand)
            {
                player.setItemInHand(Hand.OFF_HAND, stack);
            }
            else
            {
                player.setItemInHand(Hand.MAIN_HAND, stack);
            }
            hasConsumedIngredient = true;
        }
        if (needsKnife)
        {
            // offhand is not included in 'items'
            if (player.getOffhandItem().getItem().is(TFCTags.Items.KNIVES))
            {
                player.getOffhandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(Hand.OFF_HAND));
            }
            for (ItemStack invItem : player.inventory.items)
            {
                if (invItem.getItem().is(TFCTags.Items.KNIVES))
                {
                    // safe to do nothing as broadcasting break handles item use (which you can't do in the inventory)
                    invItem.hurtAndBreak(1, player, p -> {});
                    break;
                }
            }
        }
    }
}
