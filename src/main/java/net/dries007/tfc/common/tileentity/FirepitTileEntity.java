/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.ItemStackRecipeWrapper;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class FirepitTileEntity extends AbstractFirepitTileEntity<ItemStackHandler>
{
    public static final int SLOT_ITEM_INPUT = 4; // item to be cooked
    public static final int SLOT_OUTPUT_1 = 5; // generic output slot
    public static final int SLOT_OUTPUT_2 = 6; // extra output slot

    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.firepit");

    protected HeatingRecipe cachedRecipe;

    public FirepitTileEntity()
    {
        super(TFCTileEntities.FIREPIT.get(), defaultInventory(7), NAME);
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player)
    {
        return new FirepitContainer(this, playerInv, windowID);
    }

    @Override
    protected void handleCooking()
    {
        if (temperature > 0)
        {
            final ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
            inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> {
                float itemTemp = cap.getTemperature();
                HeatCapability.addTemp(cap, temperature);

                if (cachedRecipe != null && cachedRecipe.isValidTemperature(itemTemp))
                {
                    HeatingRecipe recipe = cachedRecipe;
                    ItemStackRecipeWrapper wrapper = new ItemStackRecipeWrapper(inputStack);

                    // Clear input
                    inventory.setStackInSlot(SLOT_ITEM_INPUT, ItemStack.EMPTY);

                    // Handle outputs
                    mergeOutputStack(recipe.assemble(wrapper));
                    mergeOutputFluids(recipe.getOutputFluid(wrapper), cap.getTemperature());
                }
            });
        }
    }

    @Override
    protected void coolInstantly()
    {
        inventory.getStackInSlot(SLOT_ITEM_INPUT).getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.setTemperature(0f));
    }

    @Override
    protected void updateCachedRecipe()
    {
        assert level != null;
        cachedRecipe = HeatingRecipe.getRecipe(level, new ItemStackRecipeWrapper(inventory.getStackInSlot(FirepitTileEntity.SLOT_ITEM_INPUT)));
    }

    /**
     * Merge an item stack into the two output slots
     */
    private void mergeOutputStack(ItemStack outputStack)
    {
        outputStack = inventory.insertItem(SLOT_OUTPUT_1, outputStack, false);
        if (outputStack.isEmpty())
        {
            return;
        }
        outputStack = inventory.insertItem(SLOT_OUTPUT_2, outputStack, false);
        if (outputStack.isEmpty())
        {
            return;
        }

        assert level != null;
        InventoryHelper.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), outputStack);
    }

    /**
     * Merge a fluid stack into the two output slots, treating them as fluid containers, and optionally heat containers
     */
    private void mergeOutputFluids(FluidStack fluidStack, float temperature)
    {
        fluidStack = Helpers.mergeOutputFluidIntoSlot(inventory, fluidStack, temperature, SLOT_OUTPUT_1);
        if (fluidStack.isEmpty())
        {
            return;
        }
        Helpers.mergeOutputFluidIntoSlot(inventory, fluidStack, temperature, SLOT_OUTPUT_2);
        // Any remaining fluid is lost at this point
    }
}
