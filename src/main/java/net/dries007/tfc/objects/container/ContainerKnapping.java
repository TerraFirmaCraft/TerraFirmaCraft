/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.slot.SlotKnappingOutput;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SimpleCraftMatrix;

@ParametersAreNonnullByDefault
public class ContainerKnapping extends ContainerItemStack implements IButtonHandler
{
    private final SimpleCraftMatrix matrix;
    private final KnappingType type;
    private final ItemStack stackCopy;
    public boolean requiresReset;
    private boolean hasBeenModified;

    public ContainerKnapping(KnappingType type, InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.itemIndex += 1;
        this.type = type;
        this.stackCopy = this.stack.copy();

        matrix = new SimpleCraftMatrix();
        hasBeenModified = false;
        requiresReset = false;
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable NBTTagCompound extraNBT)
    {
        matrix.set(buttonID, false);

        if (!hasBeenModified)
        {
            if (!player.isCreative() && !type.consumeAfterComplete())
            {
                ItemStack consumedStack = Helpers.consumeItem(this.stack, type.getAmountToConsume());
                if (isOffhand)
                {
                    player.setHeldItem(EnumHand.OFF_HAND, consumedStack);
                }
                else
                {
                    player.setHeldItem(EnumHand.MAIN_HAND, consumedStack);
                }
            }
            hasBeenModified = true;
        }

        // check the pattern
        Slot slot = inventorySlots.get(0);
        if (slot != null)
        {
            KnappingRecipe recipe = getMatchingRecipe();
            if (recipe != null)
            {
                slot.putStack(recipe.getOutput(this.stackCopy));
            }
            else
            {
                slot.putStack(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        Slot slot = inventorySlots.get(0);
        ItemStack stack = slot.getStack();
        if (!stack.isEmpty())
        {
            if (!player.world.isRemote)
            {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                consumeIngredientStackAfterComplete();
            }
        }
        super.onContainerClosed(player);
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
    protected void addContainerSlots()
    {
        addSlotToContainer(new SlotKnappingOutput(new ItemStackHandler(1), 0, 128, 44, this::resetMatrix));
    }

    @Override
    protected void addPlayerInventorySlots(InventoryPlayer playerInv)
    {
        // Add Player Inventory Slots (lower down)
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 18));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142 + 18));
        }
    }

    private void resetMatrix()
    {
        matrix.setAll(false);
        requiresReset = true;
        consumeIngredientStackAfterComplete();
    }

    private KnappingRecipe getMatchingRecipe()
    {
        return TFCRegistries.KNAPPING.getValuesCollection().stream().filter(x -> x.getType() == type && matrix.matches(x.getMatrix())).findFirst().orElse(null);
    }

    private void consumeIngredientStackAfterComplete()
    {
        if (type.consumeAfterComplete())
        {
            ItemStack stack = Helpers.consumeItem(this.stack, type.getAmountToConsume());
            if (isOffhand)
            {
                player.setHeldItem(EnumHand.OFF_HAND, stack);
            }
            else
            {
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
            }
        }
    }
}
