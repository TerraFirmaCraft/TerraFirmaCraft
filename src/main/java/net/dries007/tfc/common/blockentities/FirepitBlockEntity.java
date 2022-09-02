/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class FirepitBlockEntity extends AbstractFirepitBlockEntity<ItemStackHandler>
{
    public static final int SLOT_ITEM_INPUT = 4; // item to be cooked
    public static final int SLOT_OUTPUT_1 = 5; // generic output slot
    public static final int SLOT_OUTPUT_2 = 6; // extra output slot

    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.firepit");

    @Nullable protected HeatingRecipe cachedRecipe;

    public FirepitBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.FIREPIT.get(), pos, state, defaultInventory(7), NAME);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInv, Player player)
    {
        return FirepitContainer.create(this, playerInv, windowID);
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
                    final HeatingRecipe recipe = cachedRecipe;
                    final ItemStackInventory inventory = new ItemStackInventory(inputStack);

                    // Clear input
                    this.inventory.setStackInSlot(SLOT_ITEM_INPUT, ItemStack.EMPTY);

                    // Handle outputs
                    mergeOutputStack(recipe.assemble(inventory));
                    mergeOutputFluids(recipe.assembleFluid(inventory), cap.getTemperature());
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
        cachedRecipe = HeatingRecipe.getRecipe(new ItemStackInventory(inventory.getStackInSlot(FirepitBlockEntity.SLOT_ITEM_INPUT)));
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
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), outputStack);
    }

    /**
     * Merge a fluid stack into the two output slots, treating them as fluid containers, and optionally heat containers
     */
    private void mergeOutputFluids(FluidStack fluidStack, float temperature)
    {
        fluidStack = Helpers.mergeOutputFluidIntoSlot(inventory, fluidStack, temperature, SLOT_OUTPUT_1);
        Helpers.mergeOutputFluidIntoSlot(inventory, fluidStack, temperature, SLOT_OUTPUT_2);
        // Any remaining fluid is lost at this point
    }
}
