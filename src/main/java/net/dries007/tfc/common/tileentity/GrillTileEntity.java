/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.GrillContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackRecipeWrapper;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GrillTileEntity extends AbstractFirepitTileEntity<ItemStackHandler>
{
    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;

    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.grill");

    private final HeatingRecipe[] cachedRecipes;

    public GrillTileEntity()
    {
        super(TFCTileEntities.GRILL.get(), defaultInventory(9), NAME);

        cachedRecipes = new HeatingRecipe[5];
        sidedInventory
            .on(new PartialItemHandler(inventory).insert(SLOT_FUEL_INPUT).extract(4, 5, 6, 7, 8), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).insert(4, 5, 6, 7, 8), Direction.UP);
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player)
    {
        return new GrillContainer(this, playerInv, windowID);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_FUEL_INPUT)
        {
            return FuelManager.get(stack) != null;
        }
        return slot >= SLOT_EXTRA_INPUT_START && slot <= SLOT_EXTRA_INPUT_END;
    }

    @Override
    protected void handleCooking()
    {
        for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
        {
            final ItemStack inputStack = inventory.getStackInSlot(slot);
            final int finalSlot = slot;
            inputStack.getCapability(HeatCapability.CAPABILITY, null).ifPresent(cap -> {
                HeatCapability.addTemp(cap, temperature);
                HeatingRecipe recipe = cachedRecipes[finalSlot - SLOT_EXTRA_INPUT_START];
                if (recipe != null && recipe.isValidTemperature(cap.getTemperature()))
                {
                    ItemStack output = recipe.assemble(new ItemStackRecipeWrapper(inputStack));
                    //todo: apply trait WOOD_GRILLED
                    inventory.setStackInSlot(finalSlot, output);
                    markForSync();
                }
            });
        }
    }

    @Override
    protected void coolInstantly()
    {
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.setTemperature(0));
        }
    }

    @Override
    protected void updateCachedRecipe()
    {
        assert level != null;
        for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);
            cachedRecipes[slot - SLOT_EXTRA_INPUT_START] = stack.isEmpty() ? null : HeatingRecipe.getRecipe(level, new ItemStackRecipeWrapper(stack));
        }
    }
}
